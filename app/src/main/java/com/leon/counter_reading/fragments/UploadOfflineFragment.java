package com.leon.counter_reading.fragments;

import static android.app.Activity.RESULT_OK;
import static com.leon.counter_reading.helpers.Constants.ACTION_USB_PERMISSION;
import static com.leon.counter_reading.utils.OfflineUtils.zipFileAtPath;
import static com.leon.counter_reading.utils.USBUtils.isMassStorageDevice;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.leon.counter_reading.R;
import com.leon.counter_reading.activities.UploadActivity;
import com.leon.counter_reading.adapters.SpinnerCustomAdapter;
import com.leon.counter_reading.databinding.FragmentUploadBinding;
import com.leon.counter_reading.di.view_model.CustomDialogModel;
import com.leon.counter_reading.enums.DialogType;
import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.tables.TrackingDto;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.MyDatabase;
import com.leon.counter_reading.utils.uploading.PrepareOffLoadOffline;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UploadOfflineFragment extends Fragment implements PrepareOffLoadOffline.UploadCallback {
    private final ArrayList<TrackingDto> trackingDtos = new ArrayList<>();
    private FragmentUploadBinding binding;
    private Activity activity;
    private String[] items;

    private final List<UsbDevice> detectedDevices = new ArrayList<>();
    private PendingIntent permissionIntent;
    private UsbManager usbManager;
    private boolean usb;

    public static UploadOfflineFragment newInstance() {
        return new UploadOfflineFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        trackingDtos.clear();
        trackingDtos.addAll(((UploadActivity) activity).getTrackingDtos());
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUploadBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 7896) {
            if (resultCode != RESULT_OK) return;
//            File file = new File("/storage/emulated/0/ارزیابی.pdf");
            File file = new File("/storage/emulated/0/Download/10235.zip");
            copyFile(file, data.getData());
        }
    }

    private void copyFile(File src, Uri destUri) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            bis = new BufferedInputStream(new FileInputStream(src));
            bos = new BufferedOutputStream(requireContext().getContentResolver().openOutputStream(destUri));
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

    private void initialize() {

        permissionIntent = PendingIntent.getBroadcast(requireContext(), 0,
                new Intent(ACTION_USB_PERMISSION), 0);
        items = TrackingDto.getTrackingDtoItems(trackingDtos, getString(R.string.select_one));
        setupSpinner();
        binding.imageViewUpload.setImageResource(R.drawable.img_upload_off);
        setOnButtonUploadClickListener();
    }

    private void setupSpinner() {
        SpinnerCustomAdapter spinnerCustomAdapter = new SpinnerCustomAdapter(activity, items);
        binding.spinner.setAdapter(spinnerCustomAdapter);
    }

    private boolean checkOnOffLoad() {
        int total, mane = 0, unread, alalPercent, imagesCount, voicesCount, trackNumber;
        double alalMane;
        MyDatabase myDatabase = MyApplication.getApplicationComponent().MyDatabase();
        if (binding.spinner.getSelectedItemPosition() != 0) {
            trackNumber = trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).trackNumber;
            total = myDatabase.onOffLoadDao().getOnOffLoadCount(trackNumber);
            unread = myDatabase.onOffLoadDao().getOnOffLoadUnreadCount(0, trackNumber);
            ArrayList<Integer> isManes = new ArrayList<>(myDatabase.counterStateDao().
                    getCounterStateDtosIsMane(true,
                            trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).zoneId));
            for (int i = 0; i < isManes.size(); i++) {
                mane += myDatabase.onOffLoadDao().getOnOffLoadIsManeCount(isManes.get(i), trackNumber);
            }
            alalPercent = myDatabase.trackingDao().getAlalHesabByZoneId(trackingDtos
                    .get(binding.spinner.getSelectedItemPosition() - 1).zoneId);
            alalMane = (double) mane / total * 100;
            imagesCount = myDatabase.imageDao().getUnsentImageCountByTrackNumber(trackNumber, false);
            voicesCount = myDatabase.voiceDao().getUnsentVoiceCountByTrackNumber(trackNumber, false);
        } else return false;
        if (unread > 0) {
            String message = String.format(getString(R.string.unread_number), unread);
            new CustomToast().info(message, Toast.LENGTH_LONG);
            return false;
        } else if (mane > 0 && alalMane > (double) alalPercent) {
            String message = String.format(getString(R.string.darsad_alal_1), alalPercent, new DecimalFormat("###.##").format(alalMane), mane);
            new CustomToast().info(message, Toast.LENGTH_LONG);
            return false;
        } else if (imagesCount > 0 || voicesCount > 0) {
            String message = String.format(getString(R.string.unuploaded_multimedia),
                    imagesCount, voicesCount).concat("\n")
                    .concat(getString(R.string.recommend_multimedia));
            new CustomDialogModel(DialogType.YellowRedirect, activity, message,
                    getString(R.string.dear_user),
                    getString(R.string.upload), getString(R.string.confirm), new Inline());
            return false;
        }
        return true;
    }

    private void setOnButtonUploadClickListener() {
        binding.buttonUpload.setOnClickListener(v -> {
                    File file = new File("/storage/emulated/0/Download/10235.zip");
//            File file = new File("/storage/emulated/0/ارزیابی.pdf");
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.setType("application/zip");
            intent.putExtra(Intent.EXTRA_TITLE, file.getName());
            startActivityForResult(intent, 7896);
//            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
////        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
//            intent.setType("text/*");
//            intent.addCategory(Intent.CATEGORY_DEFAULT);
//            startActivityForResult(intent, 7896);


//            if (checkOnOffLoad())
//                sendOnOffLoad();
        });
    }

    private class Inline implements CustomDialogModel.Inline {
        @Override
        public void inline() {
            sendOnOffLoad();
        }
    }

    private void sendOnOffLoad() {
//        if (detectedDevices.size() > 0) {
//            usbManager.requestPermission(detectedDevices.get(0), permissionIntent);
//        } else
        disconnectDevice();
    }

    private void checkUSBStatus() {
        detectedDevices.clear();
        usbManager = (UsbManager) requireContext().getSystemService(Context.USB_SERVICE);
        if (usbManager != null) {
            HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
            if (!deviceList.isEmpty()) {
                for (UsbDevice device : deviceList.values()) {
                    if (isMassStorageDevice(device))
                        detectedDevices.add(device);
                }
            }
        }
    }

    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            checkUSBStatus();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    final UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            usb = true;
//                            connectDevice();
                        } else {
                            usb = false;
//                            disconnectDevice();
//                            zipFileAtPath(trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).trackNumber);
                        }
                    } else {
                        usb = false;
//                        disconnectDevice();
//                        zipFileAtPath(trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).trackNumber);
                    }
                }
            }
        }
    };

    private void disconnectDevice() {
        new PrepareOffLoadOffline(activity, this,
                trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).trackNumber,
                trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).id, usb)
                .execute(activity);
    }

    private void connectDevice() {
        final UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(requireActivity());
        if (devices.length > 0) {
            new PrepareOffLoadOffline(activity, this,
                    trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).trackNumber,
                    trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).id, true)
                    .execute(activity);


//            try {
//                devices[0].init();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            final FileSystem fs = devices[0].getPartitions().get(0).getFileSystem();
//            // we always use the first partition of the device
//            final UsbFile root = fs.getRootDirectory();
////            UsbFile root = entry;
////            try {
////                UsbFile file = root.createFile("bar3.txt");
////                // write to a file
////                OutputStream os = new UsbFileOutputStream(file);
////
////                os.write("hello".getBytes());
////                os.close();
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
//            zipFileAtPath(trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).trackNumber, root);
////            zipFileAtPath(trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).trackNumber);
////            File from = new File(Environment.getExternalStoragePublicDirectory(
////                    Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+ "/" + trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).trackNumber + ".zip");
////            File to = new File(root + "/" + trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).trackNumber + ".zip");
////            Log.e("change location", String.valueOf(from.renameTo(to)));
        }
    }

    @Override
    public boolean requestPermission() {
        if (detectedDevices.size() > 0)
            usbManager.requestPermission(detectedDevices.get(0), permissionIntent);
        else {
            zipFileAtPath(trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).trackNumber);
        }
        return true;
    }


    @Override
    public void onResume() {
        super.onResume();
        final IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.addAction(ACTION_USB_PERMISSION);
        requireContext().registerReceiver(usbReceiver, filter);
        checkUSBStatus();
        if (detectedDevices.size() > 0) {
            usbManager.requestPermission(detectedDevices.get(0), permissionIntent);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        requireContext().unregisterReceiver(usbReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        trackingDtos.clear();
        items = null;
    }
}