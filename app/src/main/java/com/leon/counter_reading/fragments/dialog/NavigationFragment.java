package com.leon.counter_reading.fragments.dialog;

import static com.leon.counter_reading.enums.BundleEnum.POSITION;
import static com.leon.counter_reading.enums.DialogType.Red;
import static com.leon.counter_reading.helpers.Constants.readingData;
import static com.leon.counter_reading.utils.DifferentCompanyManager.getActiveCompanyName;
import static com.leon.counter_reading.utils.DifferentCompanyManager.getAhad;
import static com.leon.counter_reading.utils.DifferentCompanyManager.getEshterakMaxLength;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.DialogFragment;

import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.FragmentNavigationBinding;
import com.leon.counter_reading.di.view_model.CustomDialogModel;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.utils.DifferentCompanyManager;
import com.leon.counter_reading.utils.navigation.Navigating;

public class NavigationFragment extends DialogFragment implements TextWatcher {
    private static NavigationFragment instance;
    public Callback readingActivity;
    private FragmentNavigationBinding binding;
    private OnOffLoadDto onOffLoadDto;
    private int position;

    public NavigationFragment() {
    }

    public static NavigationFragment newInstance() {
        return instance;
    }

    public static NavigationFragment newInstance(int position) {
        instance = new NavigationFragment();
        final Bundle args = new Bundle();
        args.putInt(POSITION.getValue(), position);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(POSITION.getValue());
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNavigationBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    private void initialize() {
        onOffLoadDto = readingData.onOffLoadDtos.get(position);
        setTextViews();
        initializeImageViews();
        setOnButtonNavigationClickListener();
        setOnEditTextChangeListener();
    }

    private void setTextViews() {
        binding.textViewEmpty.setText(getAhad(getActiveCompanyName()).concat(getString(R.string.empty)));
        binding.editTextAccount.setFilters(new InputFilter[]{new InputFilter.LengthFilter(getEshterakMaxLength(getActiveCompanyName()))});

        binding.editTextAccount.setText(onOffLoadDto.possibleEshterak);
        if (onOffLoadDto.possibleEmpty > 0)
            binding.editTextEmpty.setText(String.valueOf(onOffLoadDto.possibleEmpty));
        binding.editTextSerialCounter.setText(onOffLoadDto.possibleCounterSerial);
        binding.editTextPhone.setText(onOffLoadDto.possiblePhoneNumber);
        binding.editTextAddress.setText(onOffLoadDto.possibleAddress);
        binding.editTextMobile.setText(onOffLoadDto.possibleMobile);
    }

    void setOnButtonNavigationClickListener() {
        binding.buttonNavigation.setOnClickListener(v -> {
            View view = null;
            boolean cancel = false;
            if (binding.editTextAccount.getText().toString().length() > 0 &&
                    binding.editTextAccount.getText().toString().length() < DifferentCompanyManager.
                            getEshterakMinLength(getActiveCompanyName())) {
                binding.editTextAccount.setError(getString(R.string.error_format));
                view = binding.editTextAccount;
                cancel = true;
            } else if (binding.editTextPhone.getText().toString().length() > 0 &&
                    binding.editTextPhone.getText().toString().length() < 8) {
                binding.editTextPhone.setError(getString(R.string.error_format));
                view = binding.editTextPhone;
                cancel = true;
            } else if (binding.editTextMobile.getText().toString().length() > 0 &&
                    (binding.editTextMobile.getText().toString().length() < 11 ||
                            !binding.editTextMobile.getText().toString().substring(0, 2).contains("09"))) {
                binding.editTextMobile.setError(getString(R.string.error_format));
                view = binding.editTextMobile;
                cancel = true;
            } else if (binding.editTextSerialCounter.getText().toString().length() > 0 &&
                    binding.editTextSerialCounter.getText().toString().length() < 3) {
                binding.editTextSerialCounter.setError(getString(R.string.error_format));
                view = binding.editTextSerialCounter;
                cancel = true;
            }
            if (cancel) {
                view.requestFocus();
            } else {
                final int possibleEmpty = binding.editTextEmpty.getText().length() > 0 ?
                        Integer.parseInt(binding.editTextEmpty.getText().toString()) : 0;
                dismiss();
                new Navigating(position, onOffLoadDto.id, possibleEmpty,
                        binding.editTextAccount.getText().toString(),
                        binding.editTextMobile.getText().toString(),
                        binding.editTextPhone.getText().toString(),
                        binding.editTextSerialCounter.getText().toString(),
                        binding.editTextAddress.getText().toString()).execute(requireActivity());
            }
        });
    }

    private void initializeImageViews() {
        binding.imageViewAccount.setImageDrawable(AppCompatResources.getDrawable(requireContext(),
                R.drawable.img_subscribe));
        binding.imageViewAddress.setImageDrawable(AppCompatResources.getDrawable(requireContext(),
                R.drawable.img_address));
        binding.imageViewCounterSerial.setImageDrawable(AppCompatResources.getDrawable(requireContext(),
                R.drawable.img_counter));
        binding.imageViewPhoneNumber.setImageDrawable(AppCompatResources.getDrawable(requireContext(),
                R.drawable.img_phone));
        binding.imageViewMobile.setImageDrawable(AppCompatResources.getDrawable(requireContext(),
                R.drawable.img_mobile));
        binding.imageViewEmpty.setImageDrawable(AppCompatResources.getDrawable(requireContext(),
                R.drawable.img_home));
    }

    private void setOnEditTextChangeListener() {
        binding.editTextAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == getEshterakMaxLength(getActiveCompanyName())) {
                    final View view = binding.editTextPhone;
                    view.requestFocus();
                }
            }
        });
        binding.editTextPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 8) {
                    final View view = binding.editTextMobile;
                    view.requestFocus();
                }
            }
        });

        binding.editTextMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 11 && s.toString().substring(0, 2).contains("09")) {
                    binding.editTextSerialCounter.requestFocus();
                } else binding.editTextMobile.setError(getString(R.string.error_format));
            }
        });
        binding.editTextSerialCounter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 15) binding.editTextAddress.requestFocus();
            }
        });
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) readingActivity = (Callback) context;
    }

    public void onResume() {
        if (getDialog() != null) {
            final WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setAttributes(params);
        } else {
            new CustomDialogModel(Red, requireContext(), getString(R.string.refresh_page),
                    getString(R.string.dear_user), getString(R.string.take_screen_shot),
                    getString(R.string.accepted));
        }
        super.onResume();
    }

    public interface Callback {
        void setResult(int position, String uuid);
    }
}