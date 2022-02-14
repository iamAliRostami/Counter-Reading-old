package com.leon.counter_reading.fragments.reading_setting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.leon.counter_reading.R;
import com.leon.counter_reading.adapters.SpinnerCustomAdapter;
import com.leon.counter_reading.databinding.FragmentReadingSettingDeleteBinding;
import com.leon.counter_reading.fragments.dialog.DeleteFragment;
import com.leon.counter_reading.fragments.dialog.ShowFragmentDialog;
import com.leon.counter_reading.tables.TrackingDto;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ReadingSettingDeleteFragment extends Fragment {
    private final ArrayList<TrackingDto> trackingDtos = new ArrayList<>();
    private FragmentReadingSettingDeleteBinding binding;
    private String[] items;


    public ReadingSettingDeleteFragment(ArrayList<TrackingDto> trackingDtos) {
        this.trackingDtos.addAll(trackingDtos);
    }

    public static ReadingSettingDeleteFragment newInstance(ArrayList<TrackingDto> trackingDtos) {
        return new ReadingSettingDeleteFragment(trackingDtos);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReadingSettingDeleteBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    void initialize() {
        initializeSpinner();
        setOnButtonDeleteClickListener();
        binding.imageViewDelete.setImageDrawable(ContextCompat
                .getDrawable(requireContext(), R.drawable.img_delete));
    }

    void setOnButtonDeleteClickListener() {
        binding.buttonDelete.setOnClickListener(v -> {
            if (binding.spinner.getSelectedItemPosition() == 0) {
                ShowFragmentDialog.ShowFragmentDialogOnce(requireContext(), "DELETE_DIALOG",
                        DeleteFragment.newInstance(""));
            } else {
                ShowFragmentDialog.ShowFragmentDialogOnce(requireContext(), "DELETE_DIALOG",
                        DeleteFragment.newInstance(trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).id));
            }
        });
    }

    void initializeSpinner() {
        items = new String[trackingDtos.size() + 1];
        if (trackingDtos.size() > 0) {
            for (int i = 0; i < trackingDtos.size(); i++) {
                items[i + 1] = String.valueOf(trackingDtos.get(i).trackNumber);
            }
        }
        items[0] = getString(R.string.all_items);
        SpinnerCustomAdapter adapter = new SpinnerCustomAdapter(requireActivity(), items);
        binding.spinner.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.imageViewDelete.setImageDrawable(null);
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        items = null;
        trackingDtos.clear();
    }
}