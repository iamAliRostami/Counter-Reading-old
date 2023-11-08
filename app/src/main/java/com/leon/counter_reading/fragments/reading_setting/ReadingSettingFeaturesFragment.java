package com.leon.counter_reading.fragments.reading_setting;

import static com.leon.counter_reading.enums.ImageQuality.HIGH;
import static com.leon.counter_reading.enums.ImageQuality.LOW;
import static com.leon.counter_reading.enums.ImageQuality.MEDIUM;
import static com.leon.counter_reading.enums.SharedReferenceKeys.DONT_SHOW;
import static com.leon.counter_reading.enums.SharedReferenceKeys.GO_LAST_READ;
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
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        if (getApplicationComponent().SharedPreferenceModel().getBoolData(THEME_TEMPORARY.getValue())) {
            binding.linearLayoutDark.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_blue_2));
            binding.linearLayoutLight.setBackgroundResource(0);
            binding.textViewThemeDark.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue));
            binding.textViewThemeLight.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color_dark));
        } else {
            binding.linearLayoutLight.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_blue_2));
            binding.linearLayoutDark.setBackgroundResource(0);
            binding.textViewThemeLight.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue));
            binding.textViewThemeDark.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color_dark));
        }


//        binding.imageViewReverse.setImageDrawable(ContextCompat.getDrawable(requireContext(),
//                getApplicationComponent().SharedPreferenceModel().getBoolData(THEME_TEMPORARY.getValue()) ?
//                        R.drawable.mode_dark : R.drawable.mode_light));
//        binding.imageViewReverse.setOnClickListener(view -> {
//            getApplicationComponent().SharedPreferenceModel().putData(THEME_TEMPORARY.getValue(),
//                    !getApplicationComponent().SharedPreferenceModel().getBoolData(THEME_TEMPORARY.getValue()));
//            binding.imageViewReverse.setImageDrawable(ContextCompat.getDrawable(requireContext(),
//                    getApplicationComponent().SharedPreferenceModel().getBoolData(THEME_TEMPORARY.getValue()) ?
//                            R.drawable.mode_dark : R.drawable.mode_light));
//        });
        binding.linearLayoutDark.setOnClickListener(v -> {
            getApplicationComponent().SharedPreferenceModel().putData(THEME_TEMPORARY.getValue(), true);
            initializeImageViewReverse();
        });
        binding.linearLayoutLight.setOnClickListener(v -> {
            getApplicationComponent().SharedPreferenceModel().putData(THEME_TEMPORARY.getValue(), false);
            initializeImageViewReverse();
        });
    }

    private void initializeCheckbox() {
        binding.checkBoxPagingRotation.setChecked(getApplicationComponent().SharedPreferenceModel().getBoolData(RTL_PAGING.getValue()));
        binding.checkBoxPagingRotation.setOnClickListener(v ->
                getApplicationComponent().SharedPreferenceModel().putData(RTL_PAGING.getValue(), binding.checkBoxPagingRotation.isChecked()));
    }

    private void initializeRadioGroup() {
        initializeRadioGroupKeyboard();
        initializeRadioGroupImage();
        initializeRadioGroupPage();
    }

    private void initializeRadioGroupKeyboard() {
        if (getApplicationComponent().SharedPreferenceModel().getBoolData(KEYBOARD_TYPE.getValue()))
            binding.radioButtonSensitive.setChecked(true);
        else
            binding.radioButtonStandard.setChecked(true);
        binding.radioButtonSensitive.setOnCheckedChangeListener((compoundButton, b) ->
                getApplicationComponent().SharedPreferenceModel().putData(KEYBOARD_TYPE.getValue(), b));
    }

    private void initializeRadioGroupImage() {
        int quality = getApplicationComponent().SharedPreferenceModel().getIntData(IMAGE_QUALITY.getValue());
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

    private void initializeRadioGroupPage() {
        if (!getApplicationComponent().SharedPreferenceModel().getBoolData(DONT_SHOW.getValue())) {
            binding.radioButtonAsk.setChecked(true);
        } else if (getApplicationComponent().SharedPreferenceModel().getBoolData(GO_LAST_READ.getValue())) {
            binding.radioButtonLast.setChecked(true);
        } else if (!getApplicationComponent().SharedPreferenceModel().getBoolData(GO_LAST_READ.getValue())) {
            binding.radioButtonStay.setChecked(true);
        }
        binding.radioGroupLastRead.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.radio_button_ask) {
                getApplicationComponent().SharedPreferenceModel().putData(GO_LAST_READ.getValue(), false);
                getApplicationComponent().SharedPreferenceModel().putData(DONT_SHOW.getValue(), false);
            } else if (i == R.id.radio_button_last) {
                getApplicationComponent().SharedPreferenceModel().putData(GO_LAST_READ.getValue(), true);
                getApplicationComponent().SharedPreferenceModel().putData(DONT_SHOW.getValue(), true);
            } else if (i == R.id.radio_button_stay) {
                getApplicationComponent().SharedPreferenceModel().putData(GO_LAST_READ.getValue(), false);
                getApplicationComponent().SharedPreferenceModel().putData(DONT_SHOW.getValue(), true);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}