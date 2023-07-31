package com.leon.counter_reading.fragments.dialog;

import static com.leon.counter_reading.enums.BundleEnum.BILL_ID;
import static com.leon.counter_reading.enums.BundleEnum.POSITION;
import static com.leon.counter_reading.helpers.Constants.readingData;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.utils.MakeNotification.makeRing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.fragment.app.DialogFragment;

import com.leon.counter_reading.adapters.SpinnerAdapter;
import com.leon.counter_reading.databinding.FragmentKarbariBinding;
import com.leon.counter_reading.enums.NotificationType;
import com.leon.counter_reading.tables.KarbariDto;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class KarbariFragment extends DialogFragment {
    private ArrayList<KarbariDto> karbariDtos;
    private FragmentKarbariBinding binding;
    private String uuid;
    private int position;

    public static KarbariFragment newInstance(String uuid,int position) {
        final KarbariFragment fragment = new KarbariFragment();
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
        binding = FragmentKarbariBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    private void initialize() {
        makeRing(requireContext(), NotificationType.OTHER);
        initializeSpinner();
        setOnButtonClickListener();
    }

    private void initializeSpinner() {
        karbariDtos = new ArrayList<>(getApplicationComponent().MyDatabase().
                karbariDao().getAllKarbariDto());
        String[] items = new String[karbariDtos.size()];
        for (int i = 0; i < karbariDtos.size(); i++)
            items[i] = (karbariDtos.get(i).title);
        final SpinnerAdapter spinnerAdapter = new SpinnerAdapter(requireActivity(), items);
        binding.spinner.setAdapter(spinnerAdapter);
    }

    private void setOnButtonClickListener() {
        binding.buttonClose.setOnClickListener(v -> dismiss());
        binding.buttonSubmit.setOnClickListener(v -> {
            int id = karbariDtos.get(binding.spinner.getSelectedItemPosition()).moshtarakinId;
            //TODO
            readingData.onOffLoadDtos.get(position).possibleKarbariCode = id;
            getApplicationComponent().MyDatabase().onOffLoadDao().updateOnOffLoad(uuid, id/*
                    karbariDtos.get(binding.spinner.getSelectedItemPosition()).moshtarakinId*/);
            dismiss();
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