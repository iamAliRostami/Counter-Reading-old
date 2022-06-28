package com.leon.counter_reading.fragments.dialog;

import static com.leon.counter_reading.enums.BundleEnum.COUNTER_STATE_CODE;
import static com.leon.counter_reading.enums.BundleEnum.COUNTER_STATE_POSITION;
import static com.leon.counter_reading.enums.BundleEnum.NUMBER;
import static com.leon.counter_reading.enums.BundleEnum.POSITION;
import static com.leon.counter_reading.enums.BundleEnum.TYPE;
import static com.leon.counter_reading.enums.DialogType.Red;
import static com.leon.counter_reading.utils.MakeNotification.makeRing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.leon.counter_reading.R;
import com.leon.counter_reading.activities.ReadingActivity;
import com.leon.counter_reading.databinding.FragmentAreYouSureBinding;
import com.leon.counter_reading.di.view_model.CustomDialogModel;
import com.leon.counter_reading.enums.HighLowStateEnum;
import com.leon.counter_reading.enums.NotificationType;
import com.leon.counter_reading.utils.CustomToast;

import org.jetbrains.annotations.NotNull;

public class AreYouSureFragment extends DialogFragment {
    private FragmentAreYouSureBinding binding;
    private int position, currentNumber, type, counterStateCode, counterStatePosition;

    public static AreYouSureFragment newInstance(int position, int number, int type,
                                                 int counterStateCode, int counterStatePosition) {
        final AreYouSureFragment fragment = new AreYouSureFragment();
        final Bundle args = new Bundle();
        args.putInt(POSITION.getValue(), position);
        args.putInt(NUMBER.getValue(), number);
        args.putInt(TYPE.getValue(), type);
        args.putInt(COUNTER_STATE_CODE.getValue(), counterStateCode);
        args.putInt(COUNTER_STATE_POSITION.getValue(), counterStatePosition);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentNumber = getArguments().getInt(NUMBER.getValue());
            position = getArguments().getInt(POSITION.getValue());
            type = getArguments().getInt(TYPE.getValue());
            counterStateCode = getArguments().getInt(COUNTER_STATE_CODE.getValue());
            counterStatePosition = getArguments().getInt(COUNTER_STATE_POSITION.getValue());
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
                    updateOnOffLoadByNumber(position, currentNumber, counterStateCode,
                            counterStatePosition, type);
        });
        binding.buttonClose.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onResume() {
        if (getDialog() != null) {
            final WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setAttributes(params);
        } else {
            ((ReadingActivity) requireActivity()).updateOnOffLoadByAttempt(position, true);
            new CustomDialogModel(Red, requireContext(), getString(R.string.refresh_page),
                    getString(R.string.dear_user), getString(R.string.take_screen_shot),
                    getString(R.string.accepted));
        }
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}