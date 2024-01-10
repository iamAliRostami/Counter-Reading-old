package com.leon.counter_reading.fragments;

import static com.leon.counter_reading.enums.BundleEnum.NAME;
import static com.leon.counter_reading.enums.BundleEnum.NUMBER;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.fragment.app.DialogFragment;

import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.FragmentContactUserBinding;
import com.leon.counter_reading.view_models.CallViewModel;

import org.jetbrains.annotations.NotNull;

public class ContactUserFragment extends DialogFragment implements View.OnClickListener {
    private FragmentContactUserBinding binding;
    private CallViewModel callVM;

    public static ContactUserFragment newInstance(String name, String mobile) {
        final ContactUserFragment fragment = new ContactUserFragment();
        final Bundle args = new Bundle();
        args.putString(NUMBER.getValue(), mobile);
        args.putString(NAME.getValue(), name);
        fragment.setArguments(args);
        fragment.setCancelable(false);
        return fragment;
    }

    public ContactUserFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            callVM = new CallViewModel(getArguments().getString(NAME.getValue()),
                    getArguments().getString(NUMBER.getValue()));
            getArguments().clear();
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentContactUserBinding.inflate(inflater, container, false);
        binding.setCallVM(callVM);
        initialize();
        return binding.getRoot();
    }

    private void initialize() {
        binding.imageViewCall.setOnClickListener(this);
        binding.imageViewExit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();
        if (id == R.id.image_view_exit) {
            dismiss();
        } else if (id == R.id.image_view_call) {
            final Intent phone_intent = new Intent(Intent.ACTION_CALL);
            phone_intent.setData(Uri.parse("tel:" + callVM.getPhoneNumber()));
            startActivity(phone_intent);
        }
    }

    @Override
    public void onResume() {
        if (getDialog() != null && getDialog().getWindow() != null) {
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