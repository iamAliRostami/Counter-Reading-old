package com.leon.counter_reading.activities;

import static com.leon.counter_reading.enums.SharedReferenceKeys.AVATAR;
import static com.leon.counter_reading.enums.SharedReferenceKeys.PASSWORD;
import static com.leon.counter_reading.enums.SharedReferenceKeys.USERNAME;
import static com.leon.counter_reading.helpers.MyApplication.getAndroidVersion;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.helpers.MyApplication.getSerial;
import static com.leon.counter_reading.helpers.MyApplication.setActivityComponent;
import static com.leon.counter_reading.utils.Crypto.decrypt;
import static com.leon.counter_reading.utils.CustomFile.loadImage;
import static com.leon.counter_reading.utils.DifferentCompanyManager.getActiveCompanyName;
import static com.leon.counter_reading.utils.DifferentCompanyManager.getCompanyName;
import static com.leon.counter_reading.utils.PermissionManager.forceClose;
import static com.leon.counter_reading.utils.PermissionManager.isNetworkAvailable;
import static com.leon.counter_reading.utils.login.SetProxy.insertProxy;
import static com.leon.counter_reading.utils.login.TwoStepVerification.insertPersonalCode;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Debug;
import android.text.InputType;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.leon.counter_reading.BuildConfig;
import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.ActivityLoginBinding;
import com.leon.counter_reading.di.view_model.CustomDialogModel;
import com.leon.counter_reading.enums.DialogType;
import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.login.AttemptLogin;
import com.leon.counter_reading.utils.login.AttemptRegister;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    private ISharedPreferenceManager sharedPreferenceManager;
    private ActivityLoginBinding binding;
    private Activity activity;
    private String username, password;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        setActivityComponent(activity);
        checkReadPhoneStatePermission();
    }

    private void checkReadPhoneStatePermission() {
        if (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            initialize();
        } else {
            askReadPhoneStatusPermission();
        }
    }

    private void askReadPhoneStatusPermission() {
        final PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                new CustomToast().info(activity.getString(R.string.access_granted));
                checkReadPhoneStatePermission();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                forceClose(activity);
            }
        };
        new TedPermission(activity)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage(activity.getString(R.string.confirm_permission))
                .setRationaleConfirmText(activity.getString(R.string.allow_permission))
                .setDeniedMessage(activity.getString(R.string.if_reject_permission))
                .setDeniedCloseButtonText(activity.getString(R.string.close))
                .setGotoSettingButtonText(activity.getString(R.string.allow_permission))
                .setPermissions(Manifest.permission.READ_PHONE_STATE).check();
    }

    void initialize() {
        binding.textViewVersion.setText(getString(R.string.version).concat(" ").concat(getAndroidVersion())
                .concat(" *** ").concat(BuildConfig.VERSION_NAME));
        initializeTextViewCompanyName();
        loadPreference();
        binding.imageViewPassword.setImageResource(R.drawable.img_password);
        binding.imageViewLogo.setImageResource(R.drawable.img_login_logo);

        if (sharedPreferenceManager.checkIsNotEmpty(AVATAR.getValue()))
            binding.imageViewPerson.setImageBitmap(loadImage(activity, getApplicationComponent()
                    .SharedPreferenceModel().getStringData(AVATAR.getValue())));
        else
            binding.imageViewPerson.setImageResource(R.drawable.img_profile);

        binding.imageViewUsername.setImageResource(R.drawable.img_user);
        setOnButtonLoginClickListener();
        setOnButtonLongCLickListener();
        setOnImageViewPasswordClickListener();
        setOnImageViewPerson();
        setEditTextUsernameOnFocusChangeListener();
        setEditTextPasswordOnFocusChangeListener();
    }

    private void initializeTextViewCompanyName() {
        final TextView textViewCompanyName = findViewById(R.id.text_view_company_name);
        textViewCompanyName.setText(getCompanyName(getActiveCompanyName()));
        textViewCompanyName.setOnClickListener(v -> insertProxy(activity));
    }

    private void setEditTextUsernameOnFocusChangeListener() {
        binding.editTextUsername.setOnFocusChangeListener((view, b) -> {
            binding.editTextUsername.setHint("");
            if (b) {
                binding.linearLayoutUsername.setBackground(ContextCompat
                        .getDrawable(getApplicationContext(), R.drawable.border_black_2));
                binding.editTextPassword.setTextColor(ContextCompat.getColor(activity, R.color.black));
            } else {
                binding.linearLayoutUsername.setBackground(ContextCompat
                        .getDrawable(getApplicationContext(), R.drawable.border_gray_2));
                binding.editTextPassword.setTextColor(ContextCompat.getColor(activity, R.color.gray));
            }
        });
    }

    void setEditTextPasswordOnFocusChangeListener() {
        binding.editTextPassword.setOnFocusChangeListener((view, b) -> {
            binding.editTextPassword.setHint("");
            if (b) {
                binding.linearLayoutPassword.setBackground(ContextCompat
                        .getDrawable(getApplicationContext(), R.drawable.border_black_2));
                binding.editTextPassword.setTextColor(ContextCompat.getColor(
                        getApplicationContext(), R.color.black));
            } else {
                binding.linearLayoutPassword.setBackground(ContextCompat
                        .getDrawable(getApplicationContext(), R.drawable.border_gray_2));
                binding.editTextPassword.setTextColor(ContextCompat.getColor(getApplicationContext(),
                        R.color.gray));
            }
        });
    }

    void setOnImageViewPasswordClickListener() {
        binding.imageViewPassword.setOnClickListener(v ->
                binding.imageViewPassword.setOnClickListener(view -> {
                    if (binding.editTextPassword.getInputType() != InputType.TYPE_CLASS_TEXT) {
                        binding.editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                    } else
                        binding.editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }));
    }

    void setOnImageViewPerson() {
        binding.imageViewPerson.setOnClickListener(view ->
                new CustomDialogModel(DialogType.Green, activity, getSerial(activity),
                        MyApplication.getContext().getString(R.string.serial),
                        MyApplication.getContext().getString(R.string.dear_user),
                        MyApplication.getContext().getString(R.string.accepted)));
        binding.imageViewPerson.setOnLongClickListener(view -> {
            insertPersonalCode(activity);
            return false;
        });
    }

    void setOnButtonLongCLickListener() {
        binding.buttonLogin.setOnLongClickListener(v -> {
            attempt(false);
            return false;
        });
    }

    void setOnButtonLoginClickListener() {
        binding.buttonLogin.setOnClickListener(v -> attempt(true));
    }

    void attempt(boolean isLogin) {
        View view;
        boolean cancel = false;
        if (binding.editTextUsername.getText().length() < 1) {
            binding.editTextUsername.setError(getString(R.string.error_empty));
            view = binding.editTextUsername;
            view.requestFocus();
            cancel = true;
        }
        if (!cancel && binding.editTextPassword.getText().length() < 1) {
            binding.editTextPassword.setError(getString(R.string.error_empty));
            view = binding.editTextPassword;
            view.requestFocus();
            cancel = true;
        }
        if (!cancel) {
            if (counter > 0 && !username.equals(binding.editTextUsername.getText().toString())
                    && !password.equals(binding.editTextPassword.getText().toString()))
                counter = 0;
            username = binding.editTextUsername.getText().toString();
            password = binding.editTextPassword.getText().toString();
            if (isNetworkAvailable(activity)) {
                if (isLogin) {
                    counter++;
                    if (counter < 4)
                        new AttemptLogin(username, password, getSerial(activity),
                                binding.checkBoxSave.isChecked(), binding.buttonLogin).execute(activity);
                    else
                        offlineLogin();
                } else {
                    new AttemptRegister(username, password, getSerial(activity), binding.buttonLogin).execute(activity);
                }
            } else {
                new CustomToast().warning(activity.getString(R.string.turn_internet_on));
            }
        }
    }

    void offlineLogin() {
        if (sharedPreferenceManager.getStringData(USERNAME.getValue()).equals(username) &&
                decrypt(sharedPreferenceManager.getStringData(PASSWORD.getValue())).equals(password)) {
            new CustomToast().info(getString(R.string.check_connection), Toast.LENGTH_LONG);
            final Intent intent = new Intent(activity, HomeActivity.class);
            startActivity(intent);
            finish();
        } else if (!sharedPreferenceManager.checkIsNotEmpty(USERNAME.getValue()) ||
                !sharedPreferenceManager.checkIsNotEmpty(PASSWORD.getValue())
        ) {
            new CustomToast().warning(getString(R.string.offline_error_empty), Toast.LENGTH_LONG);
        } else {
            new CustomToast().warning(getString(R.string.error_is_not_match), Toast.LENGTH_LONG);
        }
        counter = 0;
    }

    void loadPreference() {
        sharedPreferenceManager = getApplicationComponent().SharedPreferenceModel();
        if (sharedPreferenceManager.checkIsNotEmpty(USERNAME.getValue()) &&
                sharedPreferenceManager.checkIsNotEmpty(PASSWORD.getValue())) {
            binding.editTextUsername.setText(sharedPreferenceManager.getStringData(
                    USERNAME.getValue()));
            binding.editTextPassword.setText(decrypt(sharedPreferenceManager
                    .getStringData(PASSWORD.getValue())));
        }
    }

    @Override
    protected void onDestroy() {
        binding.imageViewPerson.setImageDrawable(null);
        binding.imageViewPassword.setImageDrawable(null);
        binding.imageViewLogo.setImageDrawable(null);
        binding.imageViewUsername.setImageDrawable(null);
        binding = null;
        activity = null;
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        Debug.getNativeHeapAllocatedSize();
        System.runFinalization();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Runtime.getRuntime().gc();
        System.gc();
        super.onStop();
    }
}