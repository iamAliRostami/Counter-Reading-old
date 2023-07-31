package com.leon.counter_reading.fragments.dialog;

import static com.leon.counter_reading.enums.BundleEnum.BILL_ID;
import static com.leon.counter_reading.enums.BundleEnum.POSITION;
import static com.leon.counter_reading.enums.DialogType.Red;
import static com.leon.counter_reading.helpers.Constants.readingData;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.utils.MakeNotification.makeRing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.fragment.app.DialogFragment;

import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.FragmentSerialBinding;
import com.leon.counter_reading.di.view_model.CustomDialogModel;
import com.leon.counter_reading.enums.NotificationType;

import org.jetbrains.annotations.NotNull;

public class TaviziFragment extends DialogFragment {
    private String uuid;
    private int position;
    private FragmentSerialBinding binding;

    public static TaviziFragment newInstance(String uuid, int position) {
        final TaviziFragment fragment = new TaviziFragment();
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
        binding = FragmentSerialBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    void initialize() {
        makeRing(requireContext(), NotificationType.OTHER);
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
                getApplicationComponent().MyDatabase().onOffLoadDao().updateOnOffLoad(number, uuid);
                readingData.onOffLoadDtos.get(position).possibleCounterSerial = number;
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
        } else {
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
}