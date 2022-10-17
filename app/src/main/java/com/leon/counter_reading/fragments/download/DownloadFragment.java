package com.leon.counter_reading.fragments.download;


import static com.leon.counter_reading.enums.BundleEnum.TYPE;
import static com.leon.counter_reading.utils.CustomFile.getFileSize;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.Fragment;

import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.FragmentDownloadBinding;
import com.leon.counter_reading.utils.downloading.Download;

import org.jetbrains.annotations.NotNull;

public class DownloadFragment extends Fragment {
    private long lastClickTime = 0;
    private final int[] imageSrc = {R.drawable.img_download, R.drawable.img_download_retry,
            R.drawable.img_download_off, R.drawable.img_download_special};
    private FragmentDownloadBinding binding;
    private int type;

    public static DownloadFragment newInstance(int type) {
        final DownloadFragment fragment = new DownloadFragment();
        final Bundle args = new Bundle();
        args.putInt(TYPE.getValue(), type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt(TYPE.getValue());
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDownloadBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    private void initialize() {
        binding.imageViewDownload.setImageResource(imageSrc[type]);
        setOnButtonDownloadClickListener();
    }

    private void setOnButtonDownloadClickListener() {
        binding.buttonDownload.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - lastClickTime < 1000) return;
            lastClickTime = SystemClock.elapsedRealtime();
            final long dataStorage = getFileSize(requireContext().getExternalFilesDir(null)) / (1024 * 1024);
            if (dataStorage > 250) {
                storageDialog();
            } else
                new Download().execute(requireActivity());
        });
    }

    private void storageDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(requireContext(), R.style.AlertDialogCustom));
        builder.setTitle(R.string.storage_title);
        builder.setMessage(R.string.storage_warning);
        builder.setPositiveButton(R.string.download, (dialog, which) -> {
            dialog.dismiss();
            new Download().execute(requireActivity());
        });
        builder.setNegativeButton(R.string.close, (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.imageViewDownload.setImageDrawable(null);
    }
}