package com.leon.counter_reading.fragments.dialog;

import static com.leon.counter_reading.enums.BundleEnum.COUNTER_STATE_CODE;
import static com.leon.counter_reading.enums.BundleEnum.COUNTER_STATE_POSITION;
import static com.leon.counter_reading.enums.BundleEnum.NUMBER;
import static com.leon.counter_reading.enums.BundleEnum.POSITION;
import static com.leon.counter_reading.enums.BundleEnum.TYPE;
import static com.leon.counter_reading.enums.DialogType.Red;
import static com.leon.counter_reading.enums.HighLowStateEnum.HIGH;
import static com.leon.counter_reading.enums.HighLowStateEnum.LOW;
import static com.leon.counter_reading.enums.HighLowStateEnum.ZERO;
import static com.leon.counter_reading.enums.NotificationType.OTHER;
import static com.leon.counter_reading.utils.MakeNotification.makeRing;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.FragmentAreYouSureBinding;
import com.leon.counter_reading.di.view_model.CustomDialogModel;

import org.jetbrains.annotations.NotNull;

public class AreYouSureFragment extends DialogFragment implements View.OnClickListener {
    private FragmentAreYouSureBinding binding;
    private Callback readingActivity;
    private int position, currentNumber, type, counterStateCode, counterStatePosition;

    public static AreYouSureFragment newInstance(int position, int number, int type,
                                                 int counterStateCode, int counterStatePosition) {
        AreYouSureFragment fragment = new AreYouSureFragment();
        Bundle args = new Bundle();
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
        makeRing(getContext(), OTHER);
        setTextViewMessage();
        binding.buttonSubmit.setOnClickListener(this);
        binding.buttonClose.setOnClickListener(this);
    }

    private void setTextViewMessage() {
        if (type == HIGH.getValue()) {
            binding.textViewAreYouSure.setText(getString(R.string.high_use));
            binding.buttonSubmit.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_red_1));
        } else if (type == LOW.getValue()) {
            binding.textViewAreYouSure.setText(getString(R.string.low_use));
            binding.buttonSubmit.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_yellow_1));
        } else if (type == ZERO.getValue()) {
            binding.textViewAreYouSure.setText(getString(R.string.zero_use));
            binding.buttonSubmit.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_yellow_1));
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        dismiss();
        if (id == R.id.button_submit) {
            readingActivity.updateOnOffLoadByNumber(position, currentNumber, counterStateCode,
                    counterStatePosition, type);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) readingActivity = (Callback) context;
    }

    @Override
    public void onResume() {
        if (getDialog() != null && getDialog().getWindow() != null) {
            final WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setAttributes(params);
        } else {
            readingActivity.updateOnOffLoadByAttempt(position, true);
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


    public interface Callback {
        void updateOnOffLoadByAttempt(int position, boolean... booleans);

        void updateOnOffLoadByNumber(int position, int number, int counterStateCode,
                                     int counterStatePosition, int type);
    }
}