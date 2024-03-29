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
import com.leon.counter_reading.fragments.download.DownloadOfflineFragment;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends ListFragment {
    //    private final DownloadOfflineFragment downloadFragment;
    private final HomeCallback downloadFragment;

    public HomeFragment(DownloadOfflineFragment downloadOfflineFragment) {
        this.downloadFragment = downloadOfflineFragment;
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
        final FragmentHomeBinding binding = FragmentHomeBinding.inflate(getLayoutInflater());
        initialize();
        return binding.getRoot();
    }

    private void initialize() {
        List<UsbDevice> mDetectedDevices = downloadFragment.getUsbDevices();
        List<String> showDevices = new ArrayList<>();
        for (int i = 0; i < mDetectedDevices.size(); i++) {
            showDevices.add(mDetectedDevices.get(i).getProductName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), R.layout.item_devices,
                R.id.text_view_device, showDevices);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(@NonNull ListView list, @NonNull View view, int position, long id) {
        super.onListItemClick(list, view, position, id);
        downloadFragment.requestPermission(position);
    }

    public interface HomeCallback {
        List<UsbDevice> getUsbDevices();

        void requestPermission(int pos);
    }
}