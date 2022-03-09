package com.leon.counter_reading.fragments.dialog;

import static com.leon.counter_reading.enums.BundleEnum.BILL_ID;
import static com.leon.counter_reading.enums.SharedReferenceKeys.PASSWORD_TEMP;
import static com.leon.counter_reading.enums.SharedReferenceKeys.USERNAME_TEMP;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.utils.CalendarTool.getDate;
import static com.leon.counter_reading.utils.MakeNotification.makeRing;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.fragment.app.DialogFragment;

import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.FragmentDeleteBinding;
import com.leon.counter_reading.enums.NotificationType;
import com.leon.counter_reading.utils.Crypto;
import com.leon.counter_reading.utils.CustomToast;

import org.jetbrains.annotations.NotNull;

public class DeleteFragment extends DialogFragment {
    private String id;
    private FragmentDeleteBinding binding;
    private Activity activity;

    public DeleteFragment() {
    }

    public static DeleteFragment newInstance(String id) {
        final DeleteFragment fragment = new DeleteFragment();
        final Bundle args = new Bundle();
        args.putString(BILL_ID.getValue(), id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getString(BILL_ID.getValue());
            getArguments().clear();
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDeleteBinding.inflate(inflater, container, false);
        activity = getActivity();
        initialize();
        return binding.getRoot();
    }

    private void initialize() {
        makeRing(activity, NotificationType.SAVE);
        setOnImageViewPasswordClickListener();
        setOnButtonsClickListener();
    }

    @SuppressLint("SimpleDateFormat")
    private void setOnButtonsClickListener() {
        binding.buttonSubmit.setOnClickListener(v -> {
            if (binding.editTextUsername.getText().toString().isEmpty()) {
                binding.editTextUsername.setError(getString(R.string.error_empty));
                binding.editTextUsername.requestFocus();
            } else if (binding.editTextPassword.getText().toString().isEmpty()) {
                binding.editTextPassword.setError(getString(R.string.error_empty));
                binding.editTextPassword.requestFocus();
            } else {
                final String password = binding.editTextPassword.getText().toString();
                final String username = binding.editTextUsername.getText().toString();
                if (getApplicationComponent().SharedPreferenceModel()
                        .getStringData(USERNAME_TEMP.getValue()).contains(username) &&
                        Crypto.decrypt(getApplicationComponent().SharedPreferenceModel()
                                .getStringData(PASSWORD_TEMP.getValue())).contains(password)
                ) {
                    if (id.isEmpty()) {
                        getApplicationComponent().MyDatabase().trackingDao()
                                .updateTrackingDtoByArchive(true, false, true, getDate(requireActivity()));
/*                        getApplicationComponent().MyDatabase().
                                trackingDao().updateTrackingDtoByArchive("4a5005b2-3fb8-4e03-a8a2-1ece4374a672", false, false);*/
                    } else
                        getApplicationComponent().MyDatabase().trackingDao()
                                .updateTrackingDtoByArchive(id, true, false, true, getDate(requireActivity()));
                    final Intent intent = activity.getIntent();
                    activity.finish();
                    startActivity(intent);
                } else {
                    new CustomToast().warning(getString(R.string.error_is_not_match));
                }
            }
        });
        binding.buttonClose.setOnClickListener(v -> dismiss());
    }

    private void setOnImageViewPasswordClickListener() {
        binding.imageViewPassword.setOnClickListener(
                v -> binding.imageViewPassword.setOnClickListener(view -> {
                    if (binding.editTextPassword.getInputType() != InputType.TYPE_CLASS_TEXT) {
                        binding.editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                    } else
                        binding.editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }));
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