package com.leon.counter_reading.fragments;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_PHONE_STATE;
import static com.leon.counter_reading.enums.DialogType.Green;
import static com.leon.counter_reading.enums.SharedReferenceKeys.AVATAR;
import static com.leon.counter_reading.enums.SharedReferenceKeys.PASSWORD;
import static com.leon.counter_reading.enums.SharedReferenceKeys.USERNAME;
import static com.leon.counter_reading.helpers.Constants.GPS_CODE;
import static com.leon.counter_reading.helpers.MyApplication.getAndroidVersion;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.helpers.MyApplication.getSerial;
import static com.leon.counter_reading.helpers.MyApplication.setActivityComponent;
import static com.leon.counter_reading.utils.Crypto.decrypt;
import static com.leon.counter_reading.utils.CustomFile.loadImage;
import static com.leon.counter_reading.utils.DifferentCompanyManager.getActiveCompanyName;
import static com.leon.counter_reading.utils.DifferentCompanyManager.getCompanyName;
import static com.leon.counter_reading.utils.PermissionManager.enableGpsForResult;
import static com.leon.counter_reading.utils.PermissionManager.forceClose;
import static com.leon.counter_reading.utils.PermissionManager.isNetworkAvailable;
import static com.leon.counter_reading.utils.login.SetProxy.insertProxy;
import static com.leon.counter_reading.utils.login.TwoStepVerification.insertPersonalCode;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.leon.counter_reading.BuildConfig;
import com.leon.counter_reading.R;
import com.leon.counter_reading.activities.HomeActivity;
import com.leon.counter_reading.databinding.FragmentLoginBinding;
import com.leon.counter_reading.di.view_model.CustomDialogModel;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.login.AttemptLogin;
import com.leon.counter_reading.utils.login.AttemptRegister;

import java.util.ArrayList;

public class LoginFragment extends Fragment {
    private ISharedPreferenceManager sharedPreferenceManager;
    private FragmentLoginBinding binding;
    private String username, password;
    private int counter = 0;

    public LoginFragment() {
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        setActivityComponent(requireActivity());
        checkReadPhoneStatePermission();
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeTextViewCompanyName();
    }

    private void checkReadPhoneStatePermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(requireContext(), READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            askReadPhoneStatusPermission();
        } else if (enableGpsForResult(requireActivity())) {
            initialize();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == GPS_CODE) checkReadPhoneStatePermission();
        }
    }

    private void askReadPhoneStatusPermission() {
        final PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                new CustomToast().info(getString(R.string.access_granted));
                checkReadPhoneStatePermission();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                forceClose(requireActivity());
            }
        };
        new TedPermission(requireActivity())
                .setPermissionListener(permissionlistener)
                .setRationaleMessage(getString(R.string.confirm_permission))
                .setRationaleConfirmText(getString(R.string.allow_permission))
                .setDeniedMessage(getString(R.string.if_reject_permission))
                .setDeniedCloseButtonText(getString(R.string.close))
                .setGotoSettingButtonText(getString(R.string.allow_permission))
                .setPermissions(READ_PHONE_STATE, ACCESS_FINE_LOCATION)
                .check();
    }

    private void initialize() {
        binding.textViewVersion.setText(getString(R.string.version).concat(" ").concat(getAndroidVersion())
                .concat(" *** ").concat(BuildConfig.VERSION_NAME));
        loadPreference();
        binding.imageViewPassword.setImageResource(R.drawable.img_password);
        binding.imageViewLogo.setImageResource(R.drawable.img_login_logo);

        if (sharedPreferenceManager.checkIsNotEmpty(AVATAR.getValue()))
            binding.imageViewPerson.setImageBitmap(loadImage(requireContext(), getApplicationComponent()
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
        final TextView textViewCompanyName = requireActivity().findViewById(R.id.text_view_company_name);
        textViewCompanyName.setText(getCompanyName(getActiveCompanyName()));
        textViewCompanyName.setOnClickListener(v -> insertProxy(requireContext()));
    }

    private void setEditTextUsernameOnFocusChangeListener() {
        binding.editTextUsername.setOnFocusChangeListener((view, b) -> {
            binding.editTextUsername.setHint("");
            if (b) {
                binding.linearLayoutUsername.setBackground(ContextCompat
                        .getDrawable(requireContext(), R.drawable.border_black_2));
                binding.editTextPassword.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
            } else {
                binding.linearLayoutUsername.setBackground(ContextCompat
                        .getDrawable(requireContext(), R.drawable.border_gray_2));
                binding.editTextPassword.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray));
            }
        });
    }

    private void setEditTextPasswordOnFocusChangeListener() {
        binding.editTextPassword.setOnFocusChangeListener((view, b) -> {
            binding.editTextPassword.setHint("");
            if (b) {
                binding.linearLayoutPassword.setBackground(ContextCompat
                        .getDrawable(requireContext(), R.drawable.border_black_2));
                binding.editTextPassword.setTextColor(ContextCompat.getColor(requireContext(),
                        R.color.black));
            } else {
                binding.linearLayoutPassword.setBackground(ContextCompat.getDrawable(requireContext(),
                        R.drawable.border_gray_2));
                binding.editTextPassword.setTextColor(ContextCompat.getColor(requireContext(),
                        R.color.gray));
            }
        });
    }

    private void setOnImageViewPasswordClickListener() {
        binding.imageViewPassword.setOnClickListener(view -> {
//            initializeTextViewCompanyName();
            if (binding.editTextPassword.getInputType() != InputType.TYPE_CLASS_TEXT) {
                binding.editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT);
            } else
                binding.editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
        });
    }

    private void setOnImageViewPerson() {
        binding.imageViewPerson.setOnClickListener(view ->
                new CustomDialogModel(Green, requireContext(), getSerial(requireActivity()),
                        requireContext().getString(R.string.serial),
                        requireContext().getString(R.string.dear_user),
                        requireContext().getString(R.string.accepted)));
        binding.imageViewPerson.setOnLongClickListener(view -> {
            insertPersonalCode(requireContext());
            return false;
        });
    }

    private void setOnButtonLongCLickListener() {
        binding.buttonLogin.setOnLongClickListener(v -> {
            attempt(false);
            return false;
        });
    }

    private void setOnButtonLoginClickListener() {
        binding.buttonLogin.setOnClickListener(v -> attempt(true));
    }

    private void attempt(boolean isLogin) {
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
            if (isNetworkAvailable(requireActivity())) {
                if (isLogin) {
                    counter++;
                    if (counter < 4)
                        new AttemptLogin(username, password, getSerial(requireActivity()),
                                binding.checkBoxSave.isChecked(), binding.buttonLogin).execute(requireActivity());
                    else
                        offlineLogin();
                } else {
                    new AttemptRegister(username, password, getSerial(requireActivity()), binding.buttonLogin).execute(requireActivity());
                }
            } else {
                new CustomToast().warning(getString(R.string.turn_internet_on));
            }
        }
    }

    private void offlineLogin() {
        if (sharedPreferenceManager.getStringData(USERNAME.getValue()).equals(username) &&
                decrypt(sharedPreferenceManager.getStringData(PASSWORD.getValue())).equals(password)) {
            new CustomToast().info(getString(R.string.check_connection), Toast.LENGTH_LONG);
            final Intent intent = new Intent(requireActivity(), HomeActivity.class);
            startActivity(intent);
            requireActivity().finish();
        } else if (!sharedPreferenceManager.checkIsNotEmpty(USERNAME.getValue()) ||
                !sharedPreferenceManager.checkIsNotEmpty(PASSWORD.getValue())
        ) {
            new CustomToast().warning(getString(R.string.offline_error_empty), Toast.LENGTH_LONG);
        } else {
            new CustomToast().warning(getString(R.string.error_is_not_match), Toast.LENGTH_LONG);
        }
        counter = 0;
    }

    private void loadPreference() {
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
    public void onDestroy() {
        try {
            binding.imageViewPerson.setImageDrawable(null);
            binding.imageViewPassword.setImageDrawable(null);
            binding.imageViewLogo.setImageDrawable(null);
            binding.imageViewUsername.setImageDrawable(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}