package com.leon.counter_reading.fragments;


import static android.app.Activity.RESULT_OK;
import static com.leon.counter_reading.utils.OfflineUtils.getStorageDirectories;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.FragmentDownloadBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.DownloadType;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.downloading.Download;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class DownloadFragment extends Fragment {
    private final int[] imageSrc = {R.drawable.img_download, R.drawable.img_download_retry,
            R.drawable.img_download_off, R.drawable.img_download_special};
    private FragmentDownloadBinding binding;
    private int type;
    private Activity activity;

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
//                getExternalStorageDirectories();
                String[] retArray = getStorageDirectories();
                if (retArray.length == 0) {
                    new CustomToast().error("کارت حافظه نصب نشده است.", Toast.LENGTH_LONG);
                } else {
//                    writeOnSdCard(retArray[2], 2);
                    for (String s : retArray) {
                        Log.e("path ", s);

                        new CustomToast().error("بارگیری مقدور نمیباشد.", Toast.LENGTH_LONG);
//                        readFromSdCard(s);
                    }
                }
            } else {
                new Download().execute(requireActivity());
            }
        });
//        getExternalStorageDirectories();
//        test();
    }


    private void test() {

        UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(requireContext() /* Context or Activity */);

        for (UsbMassStorageDevice device : devices) {

            // before interacting with a device you need to call init()!
            try {
                device.init();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Only uses the first partition on the device
            FileSystem currentFs = device.getPartitions().get(0).getFileSystem();
            Log.e("Capacity: ", String.valueOf(currentFs.getCapacity()));
            Log.e("Occupied Space: ", String.valueOf(currentFs.getOccupiedSpace()));
            Log.e("Free Space: ", String.valueOf(currentFs.getFreeSpace()));
            Log.e("Chunk size: ", String.valueOf(currentFs.getChunkSize()));
        }
    }


    public void getExternalStorageDirectories() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
//        intent.setType("text/*");
//        intent.setType("file/*");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        startActivityForResult(intent, 134);


//        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
////        intent.setType("application/pdf");
////        intent.putExtra(Intent.EXTRA_TITLE, "/storage/emulated/0/Download/test.txt");
//        startActivityForResult(intent, 134);


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
//            File directory = new File("/document/D661-5964:Download/10235/");
//            File directory = new File("content://com.android.externalstorage.documents/document/D661-5964/");
            File directory = new File("/tree/D661-5964:Download/");
//            File directory = new File("/storage/usbdisk/");
//            File directory = new File("/storage/UsbStorage/");
//            File directory = new File("usb://1002/UsbStorage/");
//            File directory = new File("/dev/bus/usb/001/002");
            if (directory.isDirectory())
                Log.e("here", "directory.isDirectory()");
            if (directory.exists())
                Log.e("here", "directory.exists()");
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
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 134) {
            if (data != null) {
                String FilePath = data.getData().getPath();
                String FileName = data.getData().getLastPathSegment();
                int lastPos = FilePath.length() - FileName.length();
                String Folder = FilePath.substring(0, lastPos);

                Log.e("Full Path:", FilePath);
                Log.e("Folder:", Folder);
                Log.e("FILE NAME:", FileName);


                Uri uri = data.getData();
                Log.e("Address", uri.getPath());
                Log.e("File", uri.toString());

//                Log.e("address", FileUtil.getPath(getContext(), uri));

                Log.e("address 12", new File(uri.getPath()).getPath());


                try {
                    InputStream in = getContext().getContentResolver().openInputStream(uri);
                    BufferedReader r = new BufferedReader(new InputStreamReader(in));
//                    BufferedReader r = new BufferedReader(new FileReader(uri.getPath()));
                    StringBuilder total = new StringBuilder();
                    for (String line; (line = r.readLine()) != null; ) {
                        total.append(line).append('\n');
                    }

                    String content = total.toString();
                    Log.e("content", content);


                } catch (Exception e) {
                    e.printStackTrace();
                }


//                try {
//                    OutputStream outputStream = getContext().getContentResolver().openOutputStream(uri);
//                    OutputStreamWriter r = new OutputStreamWriter(new BufferedOutputStream(outputStream));
//
//                    r.append("json");
//                    r.close();
//                    outputStream.close();
//
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }


///storage/emulated/0/
            }
        }
    }

    public void updateUI() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.imageViewDownload.setImageDrawable(null);
    }
}