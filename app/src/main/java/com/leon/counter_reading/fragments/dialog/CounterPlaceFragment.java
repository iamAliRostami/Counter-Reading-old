package com.leon.counter_reading.fragments.dialog;

import static com.leon.counter_reading.enums.BundleEnum.BILL_ID;
import static com.leon.counter_reading.enums.BundleEnum.POSITION;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.FragmentCounterPlaceBinding;


public class CounterPlaceFragment extends DialogFragment {
    private FragmentCounterPlaceBinding binding;
    private Callback readingActivity;
    private int position;
    private String uuid;

    public CounterPlaceFragment() {
    }

    public static CounterPlaceFragment newInstance(String uuid, int position) {
        CounterPlaceFragment fragment = new CounterPlaceFragment();
        Bundle args = new Bundle();
        args.putString(BILL_ID.getValue(), uuid);
        args.putInt(POSITION.getValue(), position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uuid = getArguments().getString(BILL_ID.getValue());
            position = getArguments().getInt(POSITION.getValue());
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCounterPlaceBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    private void initialize() {
        setOnButtonSubmitClickListener();
    }

    private void setOnButtonSubmitClickListener() {
        binding.buttonSubmit.setOnClickListener(v -> {
            boolean cancel = false;
            View view = null;
            if (binding.editText1.getText().toString().isEmpty()) {
                cancel = true;
                view = binding.editText1;
                binding.editText1.setError(getString(R.string.error_empty));
            }
            if (!cancel && binding.editText2.getText().toString().isEmpty()) {
                cancel = true;
                view = binding.editText2;
                binding.editText2.setError(getString(R.string.error_empty));
            }
            if (!cancel && binding.editText3.getText().toString().isEmpty()) {
                cancel = true;
                view = binding.editText3;
                binding.editText3.setError(getString(R.string.error_empty));
            }
            if (!cancel && binding.editText4.getText().toString().isEmpty()) {
                cancel = true;
                view = binding.editText4;
                binding.editText4.setError(getString(R.string.error_empty));
            }
            if (cancel) {
                view.requestFocus();
            } else {
                final String d1 = binding.editText1.getText().toString().concat(".").
                        concat(binding.editText2.getText().toString());
                final String d2 = binding.editText3.getText().toString().concat(".").
                        concat(binding.editText4.getText().toString());
                getApplicationComponent().MyDatabase().onOffLoadDao().updateOnOffLoadLocation(uuid, d1, d2);
                dismiss();
                readingActivity.setResult(position, uuid);
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) readingActivity = (Callback) context;
    }

    public void onResume() {
        if (getDialog() != null) {
            WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setAttributes(params);
        }
        super.onResume();
    }

    public interface Callback {
        void setResult(int position, String uuid);
    }
}