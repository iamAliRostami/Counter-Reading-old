package com.leon.counter_reading.fragments;


import static com.leon.counter_reading.utils.OfflineUtils.getStorageDirectories;
import static com.leon.counter_reading.utils.OfflineUtils.readFromSdCard;
import static com.leon.counter_reading.utils.OfflineUtils.writeOnSdCard;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.FragmentDownloadBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.DownloadType;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.downloading.Download;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

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
                getExternalStorageDirectories();
                String[] retArray = getStorageDirectories();
                if (retArray.length == 0) {
                    new CustomToast().error("کارت حافظه نصب نشده است.", Toast.LENGTH_LONG);
                } else {
//                    writeOnSdCard(retArray[2], 2);
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

    public void getExternalStorageDirectories() {
//        /storage/emulated/0/Android/data/com.leon.counter_reading/files/Pictures
//        writeOnSdCard("usb://1002/UsbStorage", 12);

        UsbManager usbManager;
        UsbDevice clef;
        ArrayList<File> images;

        usbManager = (UsbManager) requireActivity().getSystemService(Context.USB_SERVICE);
        clef = null;

        if (usbManager != null) {
            HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
            if (deviceList != null) {
                for (UsbDevice usbDevice : deviceList.values()) {
                    clef = usbDevice;
                }
            }
        }

        if (clef != null) {
            File directory = new File("/storage/UsbDriveA/");
//            File directory = new File("/storage/UsbStorage/");
//            File directory = new File("usb://1002/UsbStorage/");
//            File directory = new File("/dev/bus/usb/001/002");
            if (directory.canRead()) {
                images = new ArrayList<>();
                String[] imageExtensions = {"jpg", "jpeg", "png", "gif", "JPG", "JPEG", "PNG", "GIF"};
//                    Iterator<File> iterateImages = FileUtils.iterateFiles(directory, imageExtensions, true);
//                    while (iterateImages.hasNext()) {
//                        File theImage = iterateImages.next();
//                        if (!theImage.getName().startsWith(".", 0))
//                            images.add(theImage);
//                    }
//
//                    // custom process / methods... not very relevant here :
//                    imageIndex = 0;
//                    scale = 1.0f;
//                    countImgs = images.size();
//                    loadImage(imageIndex);
//                }
            }

//        try {
//            File mountFile = new File("/proc/mounts");
//            int usbFoundCount = 0;
//            int sdcardFoundCount = 0;
//            if (mountFile.exists()) {
//                Scanner usbscanner = new Scanner(mountFile);
//                while (usbscanner.hasNext()) {
//                    String line = usbscanner.nextLine();
//                    if (line.startsWith("/dev/fuse /storage/usbcard1")) {
//                        usbFoundCount = 1;
//                        Log.i("-----USB--------", "USB Connected and properly mounted---/dev/fuse /storage/usbcard1");
//                    }
//                }
//            }
//            if (mountFile.exists()) {
//                Scanner sdcardscanner = new Scanner(mountFile);
//                while (sdcardscanner.hasNext()) {
//                    String line = sdcardscanner.nextLine();
//                    if (line.startsWith("/dev/fuse /storage/sdcard1")) {
//                        sdcardFoundCount = 1;
//                        Log.i("-----USB--------", "USB Connected and properly mounted---/dev/fuse /storage/sdcard1");
//                    }
//                }
//            }
//            if (usbFoundCount == 1) {
//                Log.i("-----USB--------", "USB Connected and properly mounted");
//            } else {
//                Log.i("-----USB--------", "USB not found!!!!");
//
//            }
//            if (sdcardFoundCount == 1) {
//                Log.i("-----SDCard--------", "SDCard Connected and properly mounted");
//            } else {
//                Log.i("-----SDCard--------", "SDCard not found!!!!");
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.imageViewDownload.setImageDrawable(null);
    }
}