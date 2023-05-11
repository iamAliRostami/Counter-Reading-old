package com.leon.counter_reading.fragments.reading_setting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.leon.counter_reading.adapters.PossibleAdapter;
import com.leon.counter_reading.databinding.FragmentReadingPossibleSettingBinding;
import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;

import org.jetbrains.annotations.NotNull;

public class ReadingPossibleSettingFragment extends Fragment {
    private FragmentReadingPossibleSettingBinding binding;

    public static ReadingPossibleSettingFragment newInstance() {
        return new ReadingPossibleSettingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReadingPossibleSettingBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    private void initialize() {
        initializeRecyclerView();
    }

    private void initializeRecyclerView() {
        binding.recyclerViewPossible.setAdapter(new PossibleAdapter(requireContext()));
        binding.recyclerViewPossible.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}