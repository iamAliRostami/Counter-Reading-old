package com.leon.counter_reading.fragments;

import static com.leon.counter_reading.enums.SharedReferenceKeys.PERSONAL_CODE;
import static com.leon.counter_reading.enums.SharedReferenceKeys.PROXY;
import static com.leon.counter_reading.helpers.MyApplication.getAndroidVersion;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.helpers.MyApplication.getSerial;
import static com.leon.counter_reading.helpers.MyApplication.validate;
import static com.leon.counter_reading.utils.DifferentCompanyManager.getActiveCompanyName;
import static com.leon.counter_reading.utils.DifferentCompanyManager.getCompanyName;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.leon.counter_reading.BuildConfig;
import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.FragmentBasicBinding;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.PermissionManager;

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
        binding.textViewCompanyName.setText(getCompanyName(getActiveCompanyName()));
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
            final String ip = binding.editTextProxy.getText().toString();
            if (ip.length() == 0 || validate(ip)) {
                getApplicationComponent().SharedPreferenceModel().putData(PROXY.getValue(),
                        ip.endsWith("/") ? ip : ip.concat("/"));
                new CustomToast().success("پروکسی با موفقیت تنظیم شد.", Toast.LENGTH_LONG);
            } else {
                binding.editTextProxy.requestFocus();
                binding.editTextProxy.setError(getString(R.string.error_format));
            }
        });
    }

    private void initializeTextView() {
        if (getApplicationComponent().SharedPreferenceModel().getIntNullData(PERSONAL_CODE.getValue()) > 0)
            binding.editTextPersonalCode.setText(String.valueOf(getApplicationComponent()
                    .SharedPreferenceModel().getIntData(PERSONAL_CODE.getValue())));
        binding.textViewSerial.setText(getSerial(requireActivity()));
        binding.editTextProxy.setText(getApplicationComponent().SharedPreferenceModel().getStringData(PROXY.getValue()));
        binding.textViewAndroidVersion.setText(getAndroidVersion());
        binding.textViewAppVersion.setText(BuildConfig.VERSION_NAME);
        binding.textViewSignal.setText(getSignal());
    }

    private String getSignal() {
        final int signal = PermissionManager.getSignal(requireContext());
        if (signal < -110)
            return "بدون آنتن";
        else if (signal < -100)
            return "بسیار ضعیف";
        else if (signal < -90)
            return "ضعیف";
        else if (signal < -75)
            return "متوسط";
        else if (signal < -60)
            return "خوب";
        else
            return "عالی";
    }

    private int getDigits(String number) {
        if (!TextUtils.isEmpty(number) && TextUtils.isDigitsOnly(number)) {
            return Integer.parseInt(number);
        } else {
            return 0;
        }
    }

}