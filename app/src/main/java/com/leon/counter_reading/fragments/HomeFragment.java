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
    private FragmentHomeBinding binding;
    private List<UsbDevice> mDetectedDevices;
    private DownloadOfflineFragment downloadOfflineFragment;

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
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        initialize();
        return binding.getRoot();
    }

    private void initialize() {
        mDetectedDevices = downloadOfflineFragment.getUsbDevices();
        List<String> showDevices = new ArrayList<>();
        for (int i = 0; i < mDetectedDevices.size(); i++) {
            // API level 21
            showDevices.add(mDetectedDevices.get(i).getProductName());
        }
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getActivity(), R.layout.row_listdevices, R.id.listText, showDevices);
        // assign the list adapter
        setListAdapter(myAdapter);
    }

    @Override
    public void onListItemClick(@NonNull ListView list, @NonNull View view, int position, long id) {
        super.onListItemClick(list, view, position, id);
        String selectedDevice = (String) getListView().getItemAtPosition(position);
        downloadOfflineFragment.requestPermission(position);
    }

    public interface HomeCallback {
        public List<UsbDevice> getUsbDevices();
        public void requestPermission(int pos);
    }
}