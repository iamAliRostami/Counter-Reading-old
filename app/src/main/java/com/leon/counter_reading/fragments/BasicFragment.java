package com.leon.counter_reading.fragments;

import static com.leon.counter_reading.enums.SharedReferenceKeys.PERSONAL_CODE;
import static com.leon.counter_reading.enums.SharedReferenceKeys.PROXY;
import static com.leon.counter_reading.helpers.MyApplication.getAndroidVersion;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.helpers.MyApplication.getSerial;
import static com.leon.counter_reading.utils.DifferentCompanyManager.getActiveCompanyName;
import static com.leon.counter_reading.utils.DifferentCompanyManager.getCompanyName;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.leon.counter_reading.BuildConfig;
import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.FragmentBasicBinding;
import com.leon.counter_reading.utils.CustomToast;

public class BasicFragment extends Fragment {
    private FragmentBasicBinding binding;

    public BasicFragment() {
    }

    public static BasicFragment newInstance() {
        return new BasicFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBasicBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    private void initialize() {
        initializeTextView();
        setOnclickListeners();
    }

    private void setOnclickListeners() {
        binding.buttonSubmitPersonal.setOnClickListener(view -> {
            getApplicationComponent().SharedPreferenceModel().putData(PERSONAL_CODE.getValue(),
                    getDigits(binding.editTextPersonalCode.getText().toString()));
            new CustomToast().success("کد پرسنلی با موفقیت ثبت شد.", Toast.LENGTH_LONG);
        });

        binding.buttonSubmitProxy.setOnClickListener(view -> {
            if (proxyValidation()) {
                getApplicationComponent().SharedPreferenceModel().putData(PROXY.getValue(),
                        binding.editTextProxy.getText().toString());
                new CustomToast().success("پروکسی با موفقیت تنظیم شد.", Toast.LENGTH_LONG);
            } else {
                binding.editTextProxy.requestFocus();
                binding.editTextProxy.setError(getString(R.string.error_format));
            }
        });
    }

    private void initializeTextView() {
        if (getApplicationComponent().SharedPreferenceModel().getIntData(PERSONAL_CODE.getValue()) > 0)
            binding.editTextPersonalCode.setText(String.valueOf(getApplicationComponent()
                    .SharedPreferenceModel().getIntData(PERSONAL_CODE.getValue())));
        binding.textViewSerial.setText(getSerial(requireActivity()));
        binding.editTextProxy.setText(getApplicationComponent().SharedPreferenceModel().getStringData(PROXY.getValue()));
        binding.textViewAndroidVersion.setText(getAndroidVersion());
        binding.textViewAppVersion.setText(BuildConfig.VERSION_NAME);

        final TextView textViewCompanyName = requireActivity().findViewById(R.id.text_view_company_name);
        textViewCompanyName.setText(getCompanyName(getActiveCompanyName()));
    }

    private int getDigits(String number) {
        if (!TextUtils.isEmpty(number) && TextUtils.isDigitsOnly(number)) {
            return Integer.parseInt(number);
        } else {
            return 0;
        }
    }

    private boolean proxyValidation() {
        final String proxy = binding.editTextProxy.getText().toString();
        return proxy.length() == 0 || proxy.startsWith("https://") || proxy.startsWith("http://");
    }
}