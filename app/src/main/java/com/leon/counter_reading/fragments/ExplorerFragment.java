package com.leon.counter_reading.fragments;

import static com.leon.counter_reading.adapters.UsbFilesAdapter.isText;
import static com.leon.counter_reading.enums.SharedReferenceKeys.SORT_ASC_KEY;
import static com.leon.counter_reading.enums.SharedReferenceKeys.SORT_FILTER_KEY;
import static com.leon.counter_reading.helpers.Constants.otgViewerCachePath;
import static com.leon.counter_reading.helpers.Constants.otgViewerPath;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.utils.USBUtils.getHumanSortBy;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
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


public class ExplorerFragment extends Fragment {
    public static int sortByCurrent;
    public static boolean sortAsc;
    private final DownloadOfflineFragment downloadOfflineFragment;
    private final Deque<UsbFile> dirs = new ArrayDeque<>();
    private FragmentExplorerBinding binding;
    private UsbFilesAdapter adapter;
    private RecyclerItemClickListener listener;

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

    private void initialize() {
        sortAsc = getApplicationComponent().SharedPreferenceModel().getBoolData(SORT_ASC_KEY.getValue());
        sortByCurrent = getApplicationComponent().SharedPreferenceModel().getIntData(SORT_FILTER_KEY.getValue());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.buttonFilterBy.setOnClickListener(v -> openFilterDialog());
        binding.imageButtonOrderBy.setOnClickListener(v -> orderByTrigger());
        UsbMassStorageDevice selectedDevice = null;
        boolean mError = false;
        final UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(requireContext());
        if (devices.length > 0)
            selectedDevice = devices[0];
        if (selectedDevice != null) {
            try {
                selectedDevice.init();
                final FileSystem fs = selectedDevice.getPartitions().get(0).getFileSystem();
                // we always use the first partition of the device
                final UsbFile root = fs.getRootDirectory();
                setupRecyclerView();
                adapter = new UsbFilesAdapter(requireContext(), root, listener);
                binding.recyclerView.setAdapter(adapter);
                updateSortUI(false);
            } catch (Exception e) {
                binding.textViewError.setVisibility(View.VISIBLE);
                mError = true;
            }
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
        listener = new RecyclerItemClickListener(
                getActivity(), binding.recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                onListItemClick(position);
            }

            @Override
            public void onLongItemClick(View view, int position) {
            }
        });
        binding.recyclerView.addOnItemTouchListener(listener);
    }

    private void onListItemClick(int position) {
        final UsbFile entry = adapter.getItem(position);
        if (entry.isDirectory()) {
            dirs.push(adapter.getCurrentDir());
            doRefresh(entry);
        } else if (UsbFilesAdapter.isText(entry)) {
            copyFileToCache(entry);
        } else {
            new CustomToast().warning("فرمت  فایل انتخاب شده نادرست است.");
        }
    }

    private boolean popUsbFile() {
        try {
            UsbFile dir = dirs.pop();
            doRefresh(dir);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final String titleText = getString(R.string.sort_by);
        final SpannableStringBuilder stringBuilder = new SpannableStringBuilder(titleText);
        stringBuilder.setSpan(new ForegroundColorSpan(requireContext().getColor(R.color.text_color_dark)),
                0, titleText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setTitle(stringBuilder);
        builder.setItems(R.array.sortby, (dialog, which) -> {
            sortByCurrent = which;
            updateSortUI(true);
            dialog.dismiss();
        });
        final AlertDialog ad = builder.create();
        ad.show();
    }

    private void orderByTrigger() {
        sortAsc = !sortAsc;
        updateSortUI(true);
    }

    private void updateSortUI(boolean doSort) {
        binding.linearLayoutSortBy.setVisibility(View.VISIBLE);
        binding.buttonFilterBy.setText(getHumanSortBy(getActivity()));

        if (sortAsc)
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
        adapter.setCurrentDir(entry);
        doRefresh();
    }

    private void doRefresh() {
        try {
            if (adapter != null)
                adapter.refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
        binding.recyclerView.scrollToPosition(0);
    }

    private void saveSortFilter() {
        getApplicationComponent().SharedPreferenceModel().putData(SORT_FILTER_KEY.getValue(), sortByCurrent);
        getApplicationComponent().SharedPreferenceModel().putData(SORT_ASC_KEY.getValue(), sortAsc);
    }

    public void launchIntent(File f) {
        if (isText(f)) {
            new DownloadOffline(requireActivity(), f).execute(requireActivity());
        }
    }

    public interface ExplorerCallback {
        void onBackPress();
    }
}

