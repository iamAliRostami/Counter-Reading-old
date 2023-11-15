package com.leon.counter_reading.fragments.dialog;

import static com.leon.counter_reading.enums.BundleEnum.BILL_ID;
import static com.leon.counter_reading.enums.BundleEnum.POSITION;
import static com.leon.counter_reading.enums.NotificationType.OTHER;
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
import com.leon.counter_reading.adapters.SpinnerAdapter;
import com.leon.counter_reading.databinding.FragmentKarbariBinding;
import com.leon.counter_reading.tables.KarbariDto;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class KarbariFragment extends DialogFragment implements View.OnClickListener {
    private ArrayList<KarbariDto> karbariDtos;
    private FragmentKarbariBinding binding;
    private String uuid;
    private int position;

    public static KarbariFragment newInstance(String uuid, int position) {
        KarbariFragment fragment = new KarbariFragment();
        Bundle args = new Bundle();
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
        if (isAdded() && getContext() != null) {
            makeRing(getContext(), OTHER);
        }
        initializeSpinner();
        binding.buttonClose.setOnClickListener(this);
        binding.buttonSubmit.setOnClickListener(this);
    }

    private void initializeSpinner() {
        karbariDtos = new ArrayList<>(getApplicationComponent().MyDatabase().
                karbariDao().getAllKarbariDto());
        String[] items = new String[karbariDtos.size()];
        for (int i = 0; i < karbariDtos.size(); i++)
            items[i] = (karbariDtos.get(i).title);

        if (isAdded() && getContext() != null) {
            SpinnerAdapter spinnerAdapter = new SpinnerAdapter(getContext(), items);
            binding.spinner.setAdapter(spinnerAdapter);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button_submit) {
            int moshtarakinId = karbariDtos.get(binding.spinner.getSelectedItemPosition()).moshtarakinId;
            readingData.onOffLoadDtos.get(position).possibleKarbariCode = moshtarakinId;
            getApplicationComponent().MyDatabase().onOffLoadDao().updateOnOffLoad(uuid, moshtarakinId);
            dismiss();
        } else if (id == R.id.button_close) {
            dismiss();
        }
    }

    @Override
    public void onResume() {
        if (getDialog() != null && getDialog().getWindow() != null) {
            WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setAttributes(params);
        }
        super.onResume();
    }
}