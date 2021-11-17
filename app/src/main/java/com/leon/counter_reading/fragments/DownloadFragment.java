package com.leon.counter_reading.fragments;

import static com.leon.counter_reading.utils.OfflineUtils.getStorageDirectories;
import static com.leon.counter_reading.utils.OfflineUtils.readFromSdCard;
import static com.leon.counter_reading.utils.OfflineUtils.writeOnSdCard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.FragmentDownloadBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.DownloadType;
import com.leon.counter_reading.utils.downloading.Download;

import org.jetbrains.annotations.NotNull;

public class DownloadFragment extends Fragment {
    private final int[] imageSrc = {R.drawable.img_download, R.drawable.img_download_retry,
            R.drawable.img_download_off, R.drawable.img_download_special};
    private FragmentDownloadBinding binding;
    private int type;

    public static DownloadFragment newInstance(int type) {
        DownloadFragment fragment = new DownloadFragment();
        Bundle args = new Bundle();
        args.putInt(BundleEnum.TYPE.getValue(), type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt(BundleEnum.TYPE.getValue());
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDownloadBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    void initialize() {
        binding.imageViewDownload.setImageResource(imageSrc[type]);
        setOnButtonDownloadClickListener();
    }

    void setOnButtonDownloadClickListener() {
        binding.buttonDownload.setOnClickListener(v -> {
            if (type == DownloadType.OFFLINE.getValue()) {
                String[] retArray = getStorageDirectories();
                if (retArray.length == 0) {
                    Log.e("state", "Sdcard not Exists");
                } else {
                    for (String s : retArray) {
                        Log.e("path ", s);
                        readFromSdCard(s);
                    }
                }
            } else {
                new Download().execute(requireActivity());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.imageViewDownload.setImageDrawable(null);
    }
}