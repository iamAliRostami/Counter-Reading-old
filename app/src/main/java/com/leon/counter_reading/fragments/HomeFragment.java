package com.leon.counter_reading.fragments;

import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.ListFragment;

import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends ListFragment {
    private final DownloadOfflineFragment downloadOfflineFragment;

    public HomeFragment(DownloadOfflineFragment downloadOfflineFragment) {
        this.downloadOfflineFragment = downloadOfflineFragment;
    }

    public static HomeFragment newInstance(DownloadOfflineFragment downloadOfflineFragment) {
        return new HomeFragment(downloadOfflineFragment);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentHomeBinding binding = FragmentHomeBinding.inflate(getLayoutInflater());
        initialize();
        return binding.getRoot();
    }

    private void initialize() {
        final List<UsbDevice> mDetectedDevices = downloadOfflineFragment.getUsbDevices();
        final List<String> showDevices = new ArrayList<>();
        for (int i = 0; i < mDetectedDevices.size(); i++) {
            showDevices.add(mDetectedDevices.get(i).getProductName());
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.item_devices, R.id.text_view_device, showDevices);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(@NonNull ListView list, @NonNull View view, int position, long id) {
        super.onListItemClick(list, view, position, id);
        downloadOfflineFragment.requestPermission(position);
    }

    public interface HomeCallback {
        List<UsbDevice> getUsbDevices();

        void requestPermission(int pos);
    }
}