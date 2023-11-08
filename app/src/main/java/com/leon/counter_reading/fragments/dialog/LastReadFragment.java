package com.leon.counter_reading.fragments.dialog;

import static com.leon.counter_reading.enums.SharedReferenceKeys.DONT_SHOW;
import static com.leon.counter_reading.enums.SharedReferenceKeys.GO_LAST_READ;
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
import com.leon.counter_reading.databinding.FragmentLastReadBinding;

public class LastReadFragment extends DialogFragment implements View.OnClickListener {

    private FragmentLastReadBinding binding;
    private ICallback callback;

    public LastReadFragment() {
    }

    public static LastReadFragment newInstance() {
        LastReadFragment fragment = new LastReadFragment();
        fragment.setCancelable(false);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLastReadBinding.inflate(inflater, container, false);
        binding.buttonSubmit.setOnClickListener(this);
        binding.buttonClose.setOnClickListener(this);
        return binding.getRoot();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button_submit) {
            getApplicationComponent().SharedPreferenceModel().putData(DONT_SHOW.getValue(), binding.checkBoxDontShow.isChecked());
            getApplicationComponent().SharedPreferenceModel().putData(GO_LAST_READ.getValue(), true);
            callback.goLastRead();
            dismiss();
        } else if (id == R.id.button_close) {
            getApplicationComponent().SharedPreferenceModel().putData(DONT_SHOW.getValue(), binding.checkBoxDontShow.isChecked());
            getApplicationComponent().SharedPreferenceModel().putData(GO_LAST_READ.getValue(), false);
            callback.setAdapter();
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity)
            callback = (ICallback) context;
    }

    public interface ICallback {
        void goLastRead();

        void setAdapter();
    }
}