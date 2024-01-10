package com.leon.counter_reading.fragments.download;

import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_ATTACHED;
import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED;
import static com.leon.counter_reading.helpers.Constants.ACTION_USB_PERMISSION;
import static com.leon.counter_reading.utils.CustomFile.readData;
import static com.leon.counter_reading.utils.USBUtils.isMassStorageDevice;

import android.annotation.SuppressLint;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.FragmentOfflineDownloadBinding;
import com.leon.counter_reading.fragments.ExplorerFragment;
import com.leon.counter_reading.fragments.HomeFragment;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.downloading.DownloadOffline;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.jahnen.libaums.UsbMassStorageDevice;

public class DownloadOfflineFragment extends Fragment implements View.OnClickListener, HomeFragment.HomeCallback,
        ExplorerFragment.ExplorerCallback {
    private final List<UsbDevice> detectedDevices = new ArrayList<>();
    private FragmentOfflineDownloadBinding binding;
    private PendingIntent permissionIntent;
    private UsbManager usbManager;
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            checkUSBStatus();
            if (ACTION_USB_DEVICE_DETACHED.equals(action))
                removedUSB();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    final UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            connectDevice();
                        }
                    }
                }
            }
        }
    };

    public static DownloadOfflineFragment newInstance() {
        return new DownloadOfflineFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOfflineDownloadBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private void initialize() {
        try {
            permissionIntent = PendingIntent.getBroadcast(requireContext(), 0,
                    new Intent(ACTION_USB_PERMISSION), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        binding.buttonDownload.setOnClickListener(this);
        binding.imageViewDownload.setImageResource(R.drawable.img_download_off);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button_download) {
//            Intent intent = new Intent(Intent.ACTION_GET_CONTENT).setType("text/plain");
//            Intent intent = new Intent(Intent.ACTION_GET_CONTENT).setType("*/*");
//            intent.setType("application/pdf|application/x-excel|text/csv");

            Intent intent;
            if (android.os.Build.MANUFACTURER.equalsIgnoreCase("samsung")) {
                intent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
                intent.putExtra("CONTENT_TYPE", "text/plain");
                intent.addCategory(Intent.CATEGORY_DEFAULT);
            } else {
                String[] mimeTypes = {"text/plain"};
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            }
            importFileResultLauncher.launch(intent);
        }
    }

    private final ActivityResultLauncher<Intent> importFileResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null
                        && result.getData().getData() != null) {
                    try {
                        Uri uri = result.getData().getData();
                        InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
                        String json = readData(inputStream);
                        new DownloadOffline(requireActivity(), json).execute(requireActivity());
                    } catch (Exception e) {
                        e.printStackTrace();
                        requireActivity().runOnUiThread(() -> new CustomToast().error("فایل انتخاب شده نادرست است."));
                    }
                }
            });

    public void updateUI() {
        if (detectedDevices.size() > 0) {
//            binding.buttonDownload.setVisibility(View.GONE);
            binding.imageViewDownload.setVisibility(View.GONE);
            binding.containerBody.setVisibility(View.VISIBLE);
            final Fragment fragment = HomeFragment.newInstance(this);
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            fragmentTransaction.replace(binding.containerBody.getId(), fragment).commit();
        }
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
            updateUI();
        }
    }

    private void connectDevice() {
        final UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(requireActivity());
        if (devices.length > 0) {
            setupDevice();
        }
    }

    private void setupDevice() {
        final Fragment fragment = ExplorerFragment.newInstance(this);
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.containerBody.getId(), fragment).commit();
    }

    private void removedUSB() {
//        binding.buttonDownload.setVisibility(View.VISIBLE);
//        binding.buttonDownload.setText(R.string.no_usb_found);
        binding.imageViewDownload.setVisibility(View.VISIBLE);
        binding.containerBody.setVisibility(View.GONE);
    }

    @Override
    public void requestPermission(int pos) {
        usbManager.requestPermission(detectedDevices.get(pos), permissionIntent);
    }

    @Override
    public List<UsbDevice> getUsbDevices() {
        return detectedDevices;
    }

    @Override
    public void onBackPress() {
        updateUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        final IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(ACTION_USB_DEVICE_DETACHED);
        filter.addAction(ACTION_USB_PERMISSION);
        requireContext().registerReceiver(usbReceiver, filter);
        checkUSBStatus();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            requireContext().unregisterReceiver(usbReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        binding.imageViewDownload.setImageDrawable(null);
    }
}