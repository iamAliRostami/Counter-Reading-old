package com.leon.counter_reading.fragments.dialog;

import static com.leon.counter_reading.enums.BundleEnum.COUNTER_STATE_CODE;
import static com.leon.counter_reading.enums.BundleEnum.COUNTER_STATE_POSITION;
import static com.leon.counter_reading.enums.BundleEnum.POSITION;
import static com.leon.counter_reading.utils.MakeNotification.makeRing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.fragment.app.DialogFragment;

import com.leon.counter_reading.R;
import com.leon.counter_reading.activities.ReadingActivity;
import com.leon.counter_reading.databinding.FragmentSerialBinding;
import com.leon.counter_reading.enums.NotificationType;

import org.jetbrains.annotations.NotNull;

public class SerialFragment extends DialogFragment {
    private int position;
    private int counterStatePosition;
    private int counterStateCode;
    private FragmentSerialBinding binding;

    public static SerialFragment newInstance(int position, int counterStateCode, int counterStatePosition) {
        final SerialFragment fragment = new SerialFragment();
        final Bundle args = new Bundle();
        args.putInt(POSITION.getValue(), position);
        args.putInt(COUNTER_STATE_CODE.getValue(), counterStateCode);
        args.putInt(COUNTER_STATE_POSITION.getValue(), counterStatePosition);
        fragment.setArguments(args);
        fragment.setCancelable(false);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(POSITION.getValue());
            counterStateCode = getArguments().getInt(COUNTER_STATE_CODE.getValue());
            counterStatePosition = getArguments().getInt(COUNTER_STATE_POSITION.getValue());
            getArguments().clear();
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSerialBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    void initialize() {
        makeRing(getContext(), NotificationType.OTHER);
        setOnButtonsClickListener();
    }

    void setOnButtonsClickListener() {
        binding.buttonClose.setOnClickListener(v -> dismiss());
        binding.buttonSubmit.setOnClickListener(v -> {
            String number = binding.editTextSerial.getText().toString();
            if (number.length() > 0 && number.length() < 3) {
                View view = binding.editTextSerial;
                binding.editTextSerial.setError(getString(R.string.error_format));
                view.requestFocus();
            } else {
                ((ReadingActivity) (requireActivity())).updateOnOffLoadByCounterSerial(
                        position, counterStatePosition, counterStateCode, number);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}