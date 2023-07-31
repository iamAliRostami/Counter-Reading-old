package com.leon.counter_reading.fragments.dialog;

import static com.leon.counter_reading.enums.BundleEnum.BILL_ID;
import static com.leon.counter_reading.enums.BundleEnum.POSITION;
import static com.leon.counter_reading.enums.NotificationType.OTHER;
import static com.leon.counter_reading.helpers.Constants.readingData;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.helpers.DifferentCompanyManager.getAhad1;
import static com.leon.counter_reading.helpers.DifferentCompanyManager.getAhad2;
import static com.leon.counter_reading.utils.MakeNotification.makeRing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.fragment.app.DialogFragment;

import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.FragmentAhadBinding;

import org.jetbrains.annotations.NotNull;

public class AhadFragment extends DialogFragment {
    private FragmentAhadBinding binding;
    private String uuid;
    private int position;

    public static AhadFragment newInstance(String uuid,int position) {
        final AhadFragment fragment = new AhadFragment();
        final Bundle args = new Bundle();
        args.putString(BILL_ID.getValue(), uuid);
        args.putInt(POSITION.getValue(), position);
        fragment.setArguments(args);
        fragment.setCancelable(false);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uuid = getArguments().getString(BILL_ID.getValue());
            position = getArguments().getInt(POSITION.getValue());
            getArguments().clear();
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAhadBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    private void initialize() {
        makeRing(requireContext(), OTHER);
        setOnButtonClickListener();
        binding.editTextAhad1.setHint(getAhad1());
        binding.editTextAhad2.setHint(getAhad2());
    }

    private void setOnButtonClickListener() {
        binding.buttonClose.setOnClickListener(v -> dismiss());
        binding.buttonSubmit.setOnClickListener(v -> {
            int asli = 0, fari = 0;
            boolean cancel = false;
            if (binding.editTextAhad1.getText().toString().isEmpty() &&
                    binding.editTextAhad2.getText().toString().isEmpty()) {
                binding.editTextAhad2.setError(getString(R.string.error_empty));
                binding.editTextAhad2.setError(getString(R.string.error_empty));
                View view = binding.editTextAhad1;
                view.requestFocus();
                cancel = true;
            } else if (!binding.editTextAhad1.getText().toString().isEmpty() &&
                    !binding.editTextAhad2.getText().toString().isEmpty()) {
                asli = Integer.parseInt(binding.editTextAhad1.getText().toString());
                fari = Integer.parseInt(binding.editTextAhad2.getText().toString());
            } else {
                if (!binding.editTextAhad1.getText().toString().isEmpty()) {
                    asli = Integer.parseInt(binding.editTextAhad1.getText().toString());
                } else if (!binding.editTextAhad2.getText().toString().isEmpty()) {
                    fari = Integer.parseInt(binding.editTextAhad2.getText().toString());
                }
            }
            if (!cancel) {
                readingData.onOffLoadDtos.get(position).possibleAhadTejariOrFari = fari;
                readingData.onOffLoadDtos.get(position).possibleAhadMaskooniOrAsli = asli;
                getApplicationComponent().MyDatabase().onOffLoadDao().updateOnOffLoad(asli, fari, uuid);
                dismiss();
            }
        });
    }

    @Override
    public void onResume() {
        if (getDialog() != null) {
            final WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setAttributes(params);
        }
        super.onResume();
    }
}