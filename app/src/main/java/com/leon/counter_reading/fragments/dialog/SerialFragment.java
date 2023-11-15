package com.leon.counter_reading.fragments.dialog;

import static com.leon.counter_reading.enums.BundleEnum.POSITION;
import static com.leon.counter_reading.enums.DialogType.Red;
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
import androidx.fragment.app.DialogFragment;

import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.FragmentSerialBinding;
import com.leon.counter_reading.di.view_model.CustomDialogModel;

import org.jetbrains.annotations.NotNull;

public class SerialFragment extends DialogFragment implements View.OnClickListener {
    private Callback readingActivity;
    private FragmentSerialBinding binding;
    private int position;

    public static SerialFragment newInstance(int position) {
        final SerialFragment fragment = new SerialFragment();
        final Bundle args = new Bundle();
        args.putInt(POSITION.getValue(), position);
        fragment.setArguments(args);
        fragment.setCancelable(false);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(POSITION.getValue());
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

    private void initialize() {
        if (isAdded() && getContext() != null) {
            makeRing(getContext(), OTHER);
        }
        binding.buttonClose.setOnClickListener(this);
        binding.buttonSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();
        if (id == R.id.button_close)
            dismiss();
        else if (id == R.id.button_submit) {
            final String number = binding.editTextSerial.getText().toString();
            if (number.length() > 0 && number.length() < 3) {
                binding.editTextSerial.setError(getString(R.string.error_format));
                binding.editTextSerial.requestFocus();
            } else {
                readingActivity.updateOnOffLoadByCounterSerial(position,
                        readingActivity.getCounterStatePosition(position),
                        readingActivity.getCounterStateCode(position), number);
                dismiss();
            }
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
            WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
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
    }


    public interface Callback {
        void updateOnOffLoadByCounterSerial(int position, int counterStatePosition,
                                            int counterStateCode, String counterSerial);

        void updateOnOffLoadByAttempt(int position, boolean... booleans);

        int getCounterStateCode(int position);

        int getCounterStatePosition(int position);
    }
}