package com.leon.counter_reading.fragments.reading_setting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.leon.counter_reading.adapters.ReadingSettingAdapter;
import com.leon.counter_reading.databinding.FragmentReadingSettingActiveBinding;
import com.leon.counter_reading.tables.TrackingDto;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ReadingSettingActiveFragment extends Fragment {
    private final ArrayList<TrackingDto> trackingDtos = new ArrayList<>();
    private FragmentReadingSettingActiveBinding binding;

    public ReadingSettingActiveFragment(ArrayList<TrackingDto> trackingDtos) {
        this.trackingDtos.addAll(trackingDtos);
    }

    public ReadingSettingActiveFragment() {
    }

    public static ReadingSettingActiveFragment newInstance(ArrayList<TrackingDto> trackingDtos) {
        return new ReadingSettingActiveFragment(trackingDtos);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReadingSettingActiveBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    private void initialize() {
        setupListView();
    }

    private void setupListView() {
        if (trackingDtos.size() > 0) {
            binding.listViewRead.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            binding.listViewRead.setAdapter(new ReadingSettingAdapter(requireContext(), trackingDtos));
        } else {
            binding.listViewRead.setVisibility(View.GONE);
            binding.textViewNotFound.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        trackingDtos.clear();
    }
}