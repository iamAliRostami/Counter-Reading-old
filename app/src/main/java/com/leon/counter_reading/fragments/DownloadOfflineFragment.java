package com.leon.counter_reading.fragments;

import static com.leon.counter_reading.helpers.Constants.ACTION_USB_PERMISSION;
import static com.leon.counter_reading.utils.USBUtils.isMassStorageDevice;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.FragmentOfflineDownloadBinding;
import com.leon.counter_reading.enums.DownloadType;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DownloadOfflineFragment extends Fragment {
    private FragmentOfflineDownloadBinding binding;
    private UsbMassStorageDevice mUsbMSDevice;
    private final List<UsbDevice> mDetectedDevices = new ArrayList<>();
    private PendingIntent mPermissionIntent;
    private UsbManager mUsbManager;

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

    void initialize() {
        binding.imageViewDownload.setImageResource(R.drawable.img_download_off);
    }

    public void updateUI() {
        Log.e("here", "updateUI");
        if (mDetectedDevices.size() > 0) {
            binding.buttonDownload.setVisibility(View.GONE);
            binding.imageViewDownload.setVisibility(View.GONE);
            binding.containerBody.setVisibility(View.VISIBLE);
            Fragment fragment = DownloadFragment.newInstance(DownloadType.RETRY.getValue());
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            fragmentTransaction.replace(binding.containerBody.getId(), fragment).commit();
//            fragmentTransaction.replace(binding.containerBody.getId(), fragment).addToBackStack("").commit();
//            fragmentTransaction.commitAllowingStateLoss();


//            fragment.getView().setFocusableInTouchMode(true);
//            fragment.getView().requestFocus();
//            fragment.getView().setOnKeyListener((v, keyCode, event) -> {
//                if (keyCode == KeyEvent.KEYCODE_BACK)
//                    Log.e("here", "back clicked");
//                return false;
//            });

//            Fragment fragment1 = DownloadFragment.newInstance(DownloadType.NORMAL.getValue());
//            FragmentTransaction fragmentTransaction1 = getChildFragmentManager().beginTransaction();
//            fragmentTransaction1.replace(binding.containerBody.getId(), fragment1)/*.commit()*/;
//            fragmentTransaction1.commitAllowingStateLoss();

        }
    }

    private void checkUSBStatus() {
        mDetectedDevices.clear();
        mUsbManager = (UsbManager) requireContext().getSystemService(Context.USB_SERVICE);
        if (mUsbManager != null) {
            HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
            if (!deviceList.isEmpty()) {
                for (UsbDevice device : deviceList.values()) {
                    if (isMassStorageDevice(device))
                        mDetectedDevices.add(device);
                }
            }
            updateUI();
        }
    }

    BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e("mUsbReceiver triggered. Action ", action);
            checkUSBStatus();
            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action))
                removedUSB();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            Log.e("granted permission for device ", device.getDeviceName() + "!");
                            connectDevice(device);
                        }
                    } else {
                        Log.e("TAG", "permission denied for device " + device);
                    }
                }
            }
        }
    };


    private void connectDevice(UsbDevice device) {
        if (mUsbManager.hasPermission(device))
            Log.e("TAG", "got permission!");
        UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(requireActivity());
        if (devices.length > 0) {
            mUsbMSDevice = devices[0];
            setupDevice();
        }
    }

    private void setupDevice() {
        Log.e("here", "setupDevice");
    }

    private void removedUSB() {
        Log.e("here", "removedUSB");
        binding.buttonDownload.setVisibility(View.VISIBLE);
        binding.imageViewDownload.setVisibility(View.VISIBLE);
        binding.containerBody.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.addAction(ACTION_USB_PERMISSION);
        requireContext().registerReceiver(usbReceiver, filter);
        checkUSBStatus();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        requireContext().unregisterReceiver(usbReceiver);
        binding.imageViewDownload.setImageDrawable(null);
    }
}