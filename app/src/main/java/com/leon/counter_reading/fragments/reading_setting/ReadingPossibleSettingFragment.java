package com.leon.counter_reading.fragments.reading_setting;

import static com.leon.counter_reading.enums.SharedReferenceKeys.ACCOUNT;
import static com.leon.counter_reading.enums.SharedReferenceKeys.ADDRESS;
import static com.leon.counter_reading.enums.SharedReferenceKeys.AHAD_1;
import static com.leon.counter_reading.enums.SharedReferenceKeys.AHAD_2;
import static com.leon.counter_reading.enums.SharedReferenceKeys.AHAD_EMPTY;
import static com.leon.counter_reading.enums.SharedReferenceKeys.AHAD_TOTAL;
import static com.leon.counter_reading.enums.SharedReferenceKeys.DESCRIPTION;
import static com.leon.counter_reading.enums.SharedReferenceKeys.GUILD;
import static com.leon.counter_reading.enums.SharedReferenceKeys.IMAGE;
import static com.leon.counter_reading.enums.SharedReferenceKeys.KARBARI;
import static com.leon.counter_reading.enums.SharedReferenceKeys.MOBILE;
import static com.leon.counter_reading.enums.SharedReferenceKeys.READING_REPORT;
import static com.leon.counter_reading.enums.SharedReferenceKeys.SERIAL;
import static com.leon.counter_reading.enums.SharedReferenceKeys.SHOW_AHAD_TITLE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.FragmentReadingPossibleSettingBinding;
import com.leon.counter_reading.helpers.DifferentCompanyManager;
import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;

import org.jetbrains.annotations.NotNull;

public class ReadingPossibleSettingFragment extends Fragment {
    private ISharedPreferenceManager sharedPreferenceManager;
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

    void initialize() {
        sharedPreferenceManager = MyApplication.getApplicationComponent().SharedPreferenceModel();
        initializeCheckBoxes();
    }

    void initializeCheckBoxes() {
        binding.checkBoxAhadEmpty.setChecked(sharedPreferenceManager.getBoolData(AHAD_EMPTY.getValue()));
        binding.checkBoxAhadEmpty.setText(DifferentCompanyManager.getAhad()
                .concat(getString(R.string.empty)));

        binding.checkBoxAhad1.setChecked(sharedPreferenceManager.getBoolData(AHAD_1.getValue()));
        binding.checkBoxAhad1.setText(DifferentCompanyManager.getAhad1());
        binding.checkBoxAhad2.setChecked(sharedPreferenceManager.getBoolData(AHAD_2.getValue()));
        binding.checkBoxAhad2.setText(DifferentCompanyManager.getAhad2());
        binding.checkBoxAhadShowTitle.setChecked(sharedPreferenceManager.getBoolData(SHOW_AHAD_TITLE.getValue()));
        binding.checkBoxAhadShowTitle.setText(getString(R.string.show).concat(DifferentCompanyManager.getAhad(
        )));
        binding.checkBoxAhadTotal.setChecked(sharedPreferenceManager.getBoolData(AHAD_TOTAL.getValue()));
        binding.checkBoxAhadTotal.setText(DifferentCompanyManager.getAhadTotal());
        binding.checkBoxAccount.setChecked(sharedPreferenceManager.getBoolData(ACCOUNT.getValue()));
        binding.checkBoxAddress.setChecked(sharedPreferenceManager.getBoolData(ADDRESS.getValue()));
        binding.checkBoxMobile.setChecked(sharedPreferenceManager.getBoolData(MOBILE.getValue()));
        binding.checkBoxKarbari.setChecked(sharedPreferenceManager.getBoolData(KARBARI.getValue()));
        binding.checkBoxSerial.setChecked(sharedPreferenceManager.getBoolData(SERIAL.getValue()));
        binding.checkBoxImage.setChecked(sharedPreferenceManager.getBoolData(IMAGE.getValue()));
        binding.checkBoxDescription.setChecked(sharedPreferenceManager.getBoolData(DESCRIPTION.getValue()));
        binding.checkBoxReadingReport.setChecked(sharedPreferenceManager.getBoolData(READING_REPORT.getValue()));
        binding.checkBoxGuild.setChecked(sharedPreferenceManager.getBoolData(GUILD.getValue()));
        setCheckBoxClickListener();
    }

    void setCheckBoxClickListener() {
        binding.checkBoxSerial.setOnClickListener(v -> sharedPreferenceManager.putData(
                SERIAL.getValue(), binding.checkBoxSerial.isChecked()));
        binding.checkBoxAhadEmpty.setOnClickListener(v -> sharedPreferenceManager.putData(
                AHAD_EMPTY.getValue(), binding.checkBoxAhadEmpty.isChecked()));
        binding.checkBoxAddress.setOnClickListener(v -> sharedPreferenceManager.putData(
                ADDRESS.getValue(), binding.checkBoxAddress.isChecked()));
        binding.checkBoxAccount.setOnClickListener(v -> sharedPreferenceManager.putData(
                ACCOUNT.getValue(), binding.checkBoxAccount.isChecked()));
        binding.checkBoxAhad2.setOnClickListener(v -> sharedPreferenceManager.putData(
                AHAD_2.getValue(), binding.checkBoxAhad2.isChecked()));
        binding.checkBoxAhad1.setOnClickListener(v -> sharedPreferenceManager.putData(
                AHAD_1.getValue(), binding.checkBoxAhad1.isChecked()));
        binding.checkBoxAhadTotal.setOnClickListener(v -> sharedPreferenceManager.putData(
                AHAD_TOTAL.getValue(), binding.checkBoxAhadTotal.isChecked()));
        binding.checkBoxAhadShowTitle.setOnClickListener(v -> sharedPreferenceManager.putData(
                SHOW_AHAD_TITLE.getValue(), binding.checkBoxAhadShowTitle.isChecked()));
        binding.checkBoxMobile.setOnClickListener(v -> sharedPreferenceManager.putData(
                MOBILE.getValue(), binding.checkBoxMobile.isChecked()));
        binding.checkBoxKarbari.setOnClickListener(v -> sharedPreferenceManager.putData(
                KARBARI.getValue(), binding.checkBoxKarbari.isChecked()));
        binding.checkBoxImage.setOnClickListener(v -> sharedPreferenceManager.putData(
                IMAGE.getValue(), binding.checkBoxImage.isChecked()));
        binding.checkBoxDescription.setOnClickListener(v -> sharedPreferenceManager.putData(
                DESCRIPTION.getValue(), binding.checkBoxDescription.isChecked()));
        binding.checkBoxReadingReport.setOnClickListener(v -> sharedPreferenceManager.putData(
                READING_REPORT.getValue(), binding.checkBoxReadingReport.isChecked()));

        binding.checkBoxGuild.setOnClickListener(v -> sharedPreferenceManager.putData(
                GUILD.getValue(), binding.checkBoxGuild.isChecked()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sharedPreferenceManager = null;
    }
}