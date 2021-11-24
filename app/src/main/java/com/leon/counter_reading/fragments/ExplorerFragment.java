package com.leon.counter_reading.fragments;

import static com.leon.counter_reading.adapters.UsbFilesAdapter.isText;
import static com.leon.counter_reading.enums.SharedReferenceKeys.SORT_ASC_KEY;
import static com.leon.counter_reading.enums.SharedReferenceKeys.SORT_FILTER_KEY;
import static com.leon.counter_reading.helpers.Constants.otgViewerCachePath;
import static com.leon.counter_reading.helpers.Constants.otgViewerPath;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.utils.USBUtils.getHumanSortBy;
import static com.leon.counter_reading.utils.USBUtils.getMimetype;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.github.mjdev.libaums.fs.UsbFile;
import com.leon.counter_reading.R;
import com.leon.counter_reading.adapters.UsbFilesAdapter;
import com.leon.counter_reading.adapters.recyclerview.RecyclerItemClickListener;
import com.leon.counter_reading.databinding.FragmentExplorerBinding;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.downloading.CopyTask;
import com.leon.counter_reading.utils.downloading.DownloadOffline;
import com.leon.counter_reading.utils.uploading.CopyTaskParam;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;


public class ExplorerFragment extends Fragment {
    private final DownloadOfflineFragment downloadOfflineFragment;
    private final Deque<UsbFile> dirs = new ArrayDeque<>();
    public static int mSortByCurrent;
    public static boolean mSortAsc;

    private FragmentExplorerBinding binding;
    private UsbMassStorageDevice mSelectedDevice;
    private UsbFilesAdapter mAdapter;
    private boolean mIsShowcase = false;
    private boolean mError = false;
    private RecyclerItemClickListener mRecyclerItemClickListener;

    public ExplorerFragment(DownloadOfflineFragment downloadOfflineFragment) {
        this.downloadOfflineFragment = downloadOfflineFragment;
    }

    public static ExplorerFragment newInstance(DownloadOfflineFragment downloadOfflineFragment) {
        return new ExplorerFragment(downloadOfflineFragment);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentExplorerBinding.inflate(getLayoutInflater());
        initialize();
        return binding.getRoot();
    }
//    private final int REQUEST_FOCUS = 0;
//    private final int REQUEST_FOCUS_DELAY = 200; //ms
//    private final Handler mHandler = new Handler() {
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case REQUEST_FOCUS:
//                    if (binding.recyclerView != null)
//                        binding.recyclerView.requestFocus();
//            }
//        }
//    };

    private void initialize() {
        mSortAsc = getApplicationComponent().SharedPreferenceModel().getBoolData(SORT_ASC_KEY.getValue());
        mSortByCurrent = getApplicationComponent().SharedPreferenceModel().getIntData(SORT_FILTER_KEY.getValue());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.buttonFilterBy.setOnClickListener(v -> openFilterDialog());
        binding.imageButtonOrderBy.setOnClickListener(v -> orderByTrigger());
        mSelectedDevice = null;
        mError = false;
        final UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(requireContext());
        if (devices.length > 0)
            mSelectedDevice = devices[0];
        try {
            mSelectedDevice.init();
            // we always use the first partition of the device
            final FileSystem fs = mSelectedDevice.getPartitions().get(0).getFileSystem();
            final UsbFile root = fs.getRootDirectory();
            setupRecyclerView();
            mAdapter = new UsbFilesAdapter(requireContext(), root, mRecyclerItemClickListener);
            binding.recyclerView.setAdapter(mAdapter);
            updateSortUI(false);
            Log.d("root getCurrentDir: ", mAdapter.getCurrentDir().toString());
        } catch (Exception e) {
            binding.textViewError.setVisibility(View.VISIBLE);
            mError = true;
        }
        if (mError) {
            binding.textViewError.setVisibility(View.VISIBLE);
        } else {
            binding.recyclerView.setEmptyView(binding.linearLayoutEmpty, binding.linearLayoutSortBy);
        }
        binding.imageViewBack.setOnClickListener(v -> {
            if (popUsbFile()) return;
            downloadOfflineFragment.onBackPress();
        });
    }

    private void setupRecyclerView() {
        mRecyclerItemClickListener = new RecyclerItemClickListener(
                getActivity(), binding.recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                onListItemClick(position);
            }

            @Override
            public void onLongItemClick(View view, int position) {
            }
        });
        binding.recyclerView.addOnItemTouchListener(mRecyclerItemClickListener);
    }

    private void onListItemClick(int position) {
        final UsbFile entry = mAdapter.getItem(position);
        if (entry.isDirectory()) {
            dirs.push(mAdapter.getCurrentDir());
            doRefresh(entry);
        } else if (UsbFilesAdapter.isText(entry)) {
            mIsShowcase = false;
            copyFileToCache(entry);
        } else {
            new CustomToast().warning("فرمت  فایل انتخاب شده نادرست است.");
        }
//        try {
//
//        } catch (IOException e) {
//            new CustomToast().error(e.toString());
//        }
    }

    private boolean popUsbFile() {
        try {
            UsbFile dir = dirs.pop();
            doRefresh(dir);
            return true;
        } catch (NoSuchElementException e) {
            Log.e(e.toString(), "we should remove this fragment!");
        } catch (Exception e) {
            Log.e("error initializing mAdapter!", e.toString());
        }
        return false;
    }

    private void copyFileToCache(UsbFile entry) {
        if (!otgViewerCachePath.exists()) if (!otgViewerCachePath.mkdirs()) return;
        final int index = entry.getName().lastIndexOf(".");
        String prefix;
        String ext = "";
        if (index < 0)
            prefix = entry.getName();
        else {
            prefix = entry.getName().substring(0, index);
            ext = entry.getName().substring(index);
        }
        Log.e("ext: ", ext);
        if (prefix.length() < 3) {
            prefix += "pad";
        }
        final String fileName = prefix + ext;
        final File downloadedFile = new File(otgViewerPath, fileName);
        final File cacheFile = new File(otgViewerCachePath, fileName);
        final CopyTaskParam param = new CopyTaskParam(entry, cacheFile);

        if (!cacheFile.exists() && !downloadedFile.exists())
            new CopyTask(requireActivity(), this).execute(param);
        else
            launchIntent(cacheFile);
    }

    private void openFilterDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(getString(R.string.sort_by));
        alertDialogBuilder.setItems(R.array.sortby, (dialog, which) -> {
            mSortByCurrent = which;
            updateSortUI(true);
            dialog.dismiss();
        });
        AlertDialog ad = alertDialogBuilder.create();
        ad.show();
    }

    private void orderByTrigger() {
        mSortAsc = !mSortAsc;
        updateSortUI(true);
    }

    private void updateSortUI(boolean doSort) {
        binding.linearLayoutSortBy.setVisibility(View.VISIBLE);
        binding.buttonFilterBy.setText(getHumanSortBy(getActivity()));

        if (mSortAsc)
            binding.imageButtonOrderBy.setImageResource(R.drawable.sort_order_asc);
        else
            binding.imageButtonOrderBy.setImageResource(R.drawable.sort_order_desc);

        if (doSort)
            doSort();
    }

    private void doSort() {
        saveSortFilter();
        doRefresh();
    }

    private void doRefresh(UsbFile entry) {
        mAdapter.setCurrentDir(entry);
        doRefresh();
    }

    private void doRefresh() {
        try {
            if (mAdapter != null)
                mAdapter.refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
        binding.recyclerView.scrollToPosition(0);
//        mHandler.sendEmptyMessageDelayed(REQUEST_FOCUS, REQUEST_FOCUS_DELAY);
    }

    private void saveSortFilter() {
        getApplicationComponent().SharedPreferenceModel().putData(SORT_FILTER_KEY.getValue(), mSortByCurrent);
        getApplicationComponent().SharedPreferenceModel().putData(SORT_ASC_KEY.getValue(), mSortAsc);
    }

    public void launchIntent(File f) {
        if (isText(f)) {
            new DownloadOffline(requireActivity(),f).execute(requireActivity());
//            if (mAdapter.getCurrentDir() == null) {
//                final FileSystem fs = mSelectedDevice.getPartitions().get(0).getFileSystem();
//                final UsbFile root = fs.getRootDirectory();
////                ImageViewer.getInstance().setCurrentDirectory(root);
//            } else {
////                ImageViewer.getInstance().setCurrentDirectory(mAdapter.getCurrentDir());
//            }
////            Intent intent = new Intent(getActivity(), ImageViewerActivity.class);
////            intent.putExtra("SHOWCASE", mIsShowcase);
////            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
////            startActivityForResult(intent, REQUEST_IMAGEVIEWER);
        }
    }

    public interface ExplorerCallback {
        void onBackPress();
    }
}

