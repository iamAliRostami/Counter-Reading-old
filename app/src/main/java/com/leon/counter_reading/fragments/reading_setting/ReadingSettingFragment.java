package com.leon.counter_reading.fragments.reading_setting;

import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.leon.counter_reading.adapters.ReadingSettingAdapter;
import com.leon.counter_reading.databinding.FragmentReadingSettingBinding;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.tables.TrackingDto;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ReadingSettingFragment extends Fragment {
    private final ArrayList<TrackingDto> trackingDtos = new ArrayList<>();
    private FragmentReadingSettingBinding binding;

    public ReadingSettingFragment(ArrayList<TrackingDto> trackingDtos) {
        this.trackingDtos.addAll(trackingDtos);
    }

    public ReadingSettingFragment() {
    }

    public static ReadingSettingFragment newInstance(ArrayList<TrackingDto> trackingDtos) {
        return new ReadingSettingFragment(trackingDtos);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReadingSettingBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    private void initialize() {
        setupListView();
        initializeCheckbox();
    }

    private void setupListView() {
        if (trackingDtos.size() > 0) {
            final ReadingSettingAdapter adapter =
                    new ReadingSettingAdapter(requireContext(), trackingDtos);
            binding.listViewRead.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            binding.listViewRead.setAdapter(adapter);
        } else {
            binding.listViewRead.setVisibility(View.GONE);
            binding.textViewNotFound.setVisibility(View.VISIBLE);
        }
    }

    private void initializeCheckbox() {
        binding.checkBoxPagingRotation.setChecked(getApplicationComponent().SharedPreferenceModel()
                .getBoolData(SharedReferenceKeys.RTL_PAGING.getValue()));
        binding.checkBoxPagingRotation.setOnClickListener(v -> getApplicationComponent()
                .SharedPreferenceModel().putData(SharedReferenceKeys.RTL_PAGING.getValue(),
                        binding.checkBoxPagingRotation.isChecked()));
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