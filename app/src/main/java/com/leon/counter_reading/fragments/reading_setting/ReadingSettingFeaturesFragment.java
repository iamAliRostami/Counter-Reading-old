package com.leon.counter_reading.fragments.reading_setting;

import static com.leon.counter_reading.enums.ImageQuality.HIGH;
import static com.leon.counter_reading.enums.ImageQuality.LOW;
import static com.leon.counter_reading.enums.ImageQuality.MEDIUM;
import static com.leon.counter_reading.enums.SharedReferenceKeys.IMAGE_QUALITY;
import static com.leon.counter_reading.enums.SharedReferenceKeys.KEYBOARD_TYPE;
import static com.leon.counter_reading.enums.SharedReferenceKeys.RTL_PAGING;
import static com.leon.counter_reading.enums.SharedReferenceKeys.THEME_TEMPORARY;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.FragmentReadingSettingFeaturesBinding;

import org.jetbrains.annotations.NotNull;

public class ReadingSettingFeaturesFragment extends Fragment {
    private FragmentReadingSettingFeaturesBinding binding;

    public ReadingSettingFeaturesFragment() {
    }

    public static ReadingSettingFeaturesFragment newInstance() {
        return new ReadingSettingFeaturesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReadingSettingFeaturesBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    private void initialize() {
        initializeCheckbox();
        initializeRadioGroup();
        initializeImageViewReverse();
    }

    private void initializeImageViewReverse() {
        binding.imageViewReverse.setImageDrawable(ContextCompat.getDrawable(requireContext(),
                getApplicationComponent().SharedPreferenceModel().getBoolData(THEME_TEMPORARY.getValue()) ?
                        R.drawable.mode_dark : R.drawable.mode_light));
        binding.imageViewReverse.setOnClickListener(view -> {
            getApplicationComponent().SharedPreferenceModel().putData(THEME_TEMPORARY.getValue(),
                    !getApplicationComponent().SharedPreferenceModel().getBoolData(THEME_TEMPORARY.getValue()));
            binding.imageViewReverse.setImageDrawable(ContextCompat.getDrawable(requireContext(),
                    getApplicationComponent().SharedPreferenceModel().getBoolData(THEME_TEMPORARY.getValue()) ?
                            R.drawable.mode_dark : R.drawable.mode_light));
        });
    }

    private void initializeCheckbox() {
        binding.checkBoxPagingRotation.setChecked(getApplicationComponent().SharedPreferenceModel()
                .getBoolData(RTL_PAGING.getValue()));
        binding.checkBoxPagingRotation.setOnClickListener(v ->
                getApplicationComponent().SharedPreferenceModel().putData(RTL_PAGING.getValue(),
                        binding.checkBoxPagingRotation.isChecked()));
    }

    private void initializeRadioGroup() {
        binding.radioButtonStandard.setChecked(!getApplicationComponent().SharedPreferenceModel()
                .getBoolData(KEYBOARD_TYPE.getValue()));
        binding.radioButtonSensitive.setChecked(getApplicationComponent().SharedPreferenceModel()
                .getBoolData(KEYBOARD_TYPE.getValue()));
        binding.radioButtonSensitive.setOnCheckedChangeListener((compoundButton, b) ->
                getApplicationComponent().SharedPreferenceModel().putData(KEYBOARD_TYPE.getValue(), b));

        final int quality = getApplicationComponent().SharedPreferenceModel().getIntData(IMAGE_QUALITY.getValue());
        if (quality == HIGH.getValue()) {
            binding.radioButtonHigh.setChecked(true);
        } else if (quality == MEDIUM.getValue()) {
            binding.radioButtonMedium.setChecked(true);
        } else if (quality == LOW.getValue()) {
            binding.radioButtonLow.setChecked(true);
        }
        binding.radioGroupQuality.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.radio_button_high) {
                getApplicationComponent().SharedPreferenceModel().putData(IMAGE_QUALITY.getValue(), HIGH.getValue());
            } else if (i == R.id.radio_button_medium) {
                getApplicationComponent().SharedPreferenceModel().putData(IMAGE_QUALITY.getValue(), MEDIUM.getValue());
            } else if (i == R.id.radio_button_low) {
                getApplicationComponent().SharedPreferenceModel().putData(IMAGE_QUALITY.getValue(), LOW.getValue());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}