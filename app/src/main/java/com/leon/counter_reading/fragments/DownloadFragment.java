package com.leon.counter_reading.fragments;


import static android.app.Activity.RESULT_OK;
import static com.leon.counter_reading.utils.OfflineUtils.getStorageDirectories;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.leon.counter_reading.FileUtil;
import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.FragmentDownloadBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.DownloadType;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.downloading.Download;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
        getExternalStorageDirectories();
    }

    public void getExternalStorageDirectories() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.setType("text/*");
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
                Uri uri = data.getData();
                Log.e("Address", uri.getPath());
                Log.e("File", uri.toString());

                Log.e("address", FileUtil.getPath(getContext(), uri));





                try {
                    InputStream in = getContext().getContentResolver().openInputStream(uri);
                    BufferedReader r = new BufferedReader(new InputStreamReader(in));
                    StringBuilder total = new StringBuilder();
                    for (String line; (line = r.readLine()) != null; ) {
                        total.append(line).append('\n');
                    }

                    String content = total.toString();
                    Log.e("content", content);


                } catch (Exception e) {
                    e.printStackTrace();
                }
///storage/emulated/0/
            }
//            File fileToCopy = new File("/storage/emulated/0/Download/test.txt");
//            if (data != null) {
//                copyFile(fileToCopy, data.getData());
//            }
        }
    }

    private void copyFile(File src, Uri destUri) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            bis = new BufferedInputStream(new FileInputStream(src));
            bos = new BufferedOutputStream(requireActivity().getContentResolver().openOutputStream(destUri));
            byte[] buf = new byte[1024];
            bis.read(buf);
            do {
                bos.write(buf);
            } while (bis.read(buf) != -1);
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) bis.close();
                if (bos != null) bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.imageViewDownload.setImageDrawable(null);
    }
}