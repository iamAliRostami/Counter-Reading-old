package com.leon.counter_reading.fragments.dialog;

import static com.leon.counter_reading.utils.MakeNotification.makeRing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.leon.counter_reading.R;
import com.leon.counter_reading.activities.ReadingActivity;
import com.leon.counter_reading.databinding.FragmentAreYouSureBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.HighLowStateEnum;
import com.leon.counter_reading.enums.NotificationType;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AreYouSureFragment extends DialogFragment {
    private FragmentAreYouSureBinding binding;
    private int position, currentNumber, type, counterStateCode, counterStatePosition;

    public static AreYouSureFragment newInstance(int position, int number, int type,
                                                 int counterStateCode, int counterStatePosition
    ) {
        AreYouSureFragment fragment = new AreYouSureFragment();
        Bundle args = new Bundle();
        args.putInt(BundleEnum.POSITION.getValue(), position);
        args.putInt(BundleEnum.NUMBER.getValue(), number);
        args.putInt(BundleEnum.TYPE.getValue(), type);
        args.putInt(BundleEnum.COUNTER_STATE_CODE.getValue(), counterStateCode);
        args.putInt(BundleEnum.COUNTER_STATE_POSITION.getValue(), counterStatePosition);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentNumber = getArguments().getInt(BundleEnum.NUMBER.getValue());
            position = getArguments().getInt(BundleEnum.POSITION.getValue());
            type = getArguments().getInt(BundleEnum.TYPE.getValue());
            counterStateCode = getArguments().getInt(BundleEnum.COUNTER_STATE_CODE.getValue());
            counterStatePosition = getArguments().getInt(BundleEnum.COUNTER_STATE_POSITION.getValue());
            getArguments().clear();
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAreYouSureBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    private void initialize() {
        makeRing(getContext(), NotificationType.OTHER);
        setTextViewMessage();
        setOnButtonsClickListener();
    }

    private void setTextViewMessage() {
        if (type == HighLowStateEnum.HIGH.getValue()) {
            binding.textViewAreYouSure.setText(getString(R.string.high_use));
            binding.buttonSubmit.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_red_1));
        } else if (type == HighLowStateEnum.LOW.getValue()) {
            binding.textViewAreYouSure.setText(getString(R.string.low_use));
            binding.buttonSubmit.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_yellow_1));
        } else if (type == HighLowStateEnum.ZERO.getValue()) {
            binding.textViewAreYouSure.setText(getString(R.string.zero_use));
            binding.buttonSubmit.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_yellow_1));
        }
    }

    private void setOnButtonsClickListener() {
        binding.buttonSubmit.setOnClickListener(v -> {
            dismiss();
            ((ReadingActivity) requireActivity()).
                    updateOnOffLoadByCounterNumber(position, currentNumber, counterStateCode,
                            counterStatePosition, type);
        });
        binding.buttonClose.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onResume() {
        WindowManager.LayoutParams params = Objects.requireNonNull(getDialog()).getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(params);
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}