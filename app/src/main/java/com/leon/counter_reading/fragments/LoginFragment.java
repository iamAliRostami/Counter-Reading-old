package com.leon.counter_reading.fragments;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static com.leon.counter_reading.enums.DialogType.Green;
import static com.leon.counter_reading.enums.SharedReferenceKeys.AVATAR;
import static com.leon.counter_reading.enums.SharedReferenceKeys.PASSWORD;
import static com.leon.counter_reading.enums.SharedReferenceKeys.USERNAME;
import static com.leon.counter_reading.helpers.Constants.GPS_CODE;
import static com.leon.counter_reading.helpers.DifferentCompanyManager.getCompanyName;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.helpers.MyApplication.getSerial;
import static com.leon.counter_reading.helpers.MyApplication.setActivityComponent;
import static com.leon.counter_reading.utils.CustomFile.loadImage;
import static com.leon.counter_reading.utils.PermissionManager.enableGpsForResult;
import static com.leon.counter_reading.utils.PermissionManager.forceClose;
import static com.leon.counter_reading.utils.PermissionManager.isNetworkAvailable;
import static com.leon.counter_reading.utils.login.SetProxy.insertProxy;
import static com.leon.counter_reading.utils.login.TwoStepVerification.insertPersonalCode;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.leon.counter_reading.R;
import com.leon.counter_reading.activities.HomeActivity;
import com.leon.counter_reading.databinding.FragmentLoginBinding;
import com.leon.counter_reading.di.view_model.CustomDialogModel;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.login.AttemptLogin;
import com.leon.counter_reading.utils.login.AttemptRegister;
import com.leon.counter_reading.utils.login.CreateDNTCaptcha;
import com.leon.counter_reading.utils.login.ShowDNTCaptchaImage;
import com.leon.counter_reading.view_models.LoginViewModel;

import java.util.ArrayList;

public class LoginFragment extends Fragment implements View.OnClickListener {
    private FragmentLoginBinding binding;
    private LoginViewModel login;
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
        login = new LoginViewModel(getSerial(requireActivity()));
        binding.setLogin(login);
        checkReadPhoneStatePermission();
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeTextViewCompanyName();
    }


    private void initialize() {
        if (getApplicationComponent().SharedPreferenceModel().checkIsNotEmpty(AVATAR.getValue()))
            binding.imageViewPerson.setImageBitmap(loadImage(requireContext(), getApplicationComponent()
                    .SharedPreferenceModel().getStringData(AVATAR.getValue())));
        binding.buttonLogin.setOnClickListener(this);
        binding.imageViewPerson.setOnClickListener(this);
        binding.imageViewPassword.setOnClickListener(this);
        binding.imageViewRecaptcha.setOnClickListener(this);

        setOnButtonLongCLickListener();
        setOnImageViewPersonLongClickListener();

        setEditTextUsernameOnFocusChangeListener();
        setEditTextPasswordOnFocusChangeListener();

        createDNTCaptcha();
    }

    private void initializeTextViewCompanyName() {
        final TextView textViewCompanyName = requireActivity().findViewById(R.id.text_view_company_name);
        textViewCompanyName.setText(getCompanyName());
        textViewCompanyName.setOnClickListener(v -> insertProxy(requireContext()));
    }

    private void setEditTextUsernameOnFocusChangeListener() {
        binding.editTextUsername.setOnFocusChangeListener((view, b) -> {
            binding.editTextUsername.setHint("");
            if (b) {
                binding.linearLayoutUsername.setBackground(ContextCompat
                        .getDrawable(requireContext(), R.drawable.border_black_2));
                binding.editTextPassword.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black));
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
                        android.R.color.black));
            } else {
                binding.linearLayoutPassword.setBackground(ContextCompat.getDrawable(requireContext(),
                        R.drawable.border_gray_2));
                binding.editTextPassword.setTextColor(ContextCompat.getColor(requireContext(),
                        R.color.gray));
            }
        });
    }

    private void setOnImageViewPersonLongClickListener() {
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

    private void attempt(boolean isLogin) {
        if (inputValidation(binding.editTextUsername)) return;
        if (inputValidation(binding.editTextPassword)) return;
        if (counter > 0 && login.checkUserPasswordChange())
            counter = 0;
        login.setOldPassword(login.getPassword());
        login.setOldUsername(login.getUsername());
        if (isNetworkAvailable(requireActivity())) {
            if (isLogin) {
                counter++;
                if (counter < 4)
                    new AttemptLogin(this).execute(requireActivity());
                else
                    offlineLogin();
            } else {
                new AttemptRegister(this).execute(requireActivity());
            }
        } else {
            new CustomToast().warning(getString(R.string.turn_internet_on));
        }
    }

    private boolean inputValidation(final EditText editText) {
        if (editText.getText().toString().isEmpty()) {
            editText.setError(getString(R.string.error_empty));
            editText.requestFocus();
            return true;
        }
        return false;
    }

    public void resetAttempt() {
        createDNTCaptcha();
        login.setDntCaptchaInputText("");
        counter = 0;
    }

    private void offlineLogin() {
        if (login.checkUserPassword()) {
            new CustomToast().info(getString(R.string.check_connection), Toast.LENGTH_LONG);
            final Intent intent = new Intent(requireActivity(), HomeActivity.class);
            startActivity(intent);
            requireActivity().finish();
        } else if (!getApplicationComponent().SharedPreferenceModel().checkIsNotEmpty(USERNAME.getValue()) ||
                !getApplicationComponent().SharedPreferenceModel().checkIsNotEmpty(PASSWORD.getValue())
        ) {
            new CustomToast().warning(getString(R.string.offline_error_empty), Toast.LENGTH_LONG);
        } else {
            new CustomToast().warning(getString(R.string.error_is_not_match), Toast.LENGTH_LONG);
        }
        counter = 0;
    }

    private void createDNTCaptcha() {
        binding.imageViewCaptcha.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.not_found));
        new CreateDNTCaptcha(this).execute(requireActivity());
    }

    public void showDNTCaptchaImage(LoginViewModel login) {
        this.login.setDntCaptchaText(login.getDntCaptchaTextValue());
        this.login.setDntCaptchaToken(login.getDntCaptchaTokenValue());
        this.login.setData(login.getDntCaptchaImgUrl().substring(login.getDntCaptchaImgUrl().lastIndexOf("data=") + 5));
        new ShowDNTCaptchaImage(this, this.login).execute(requireActivity());
    }

    public void setCaptchaResult(Bitmap captchaResult) {
        final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) binding.imageViewCaptcha.getLayoutParams();
        params.height = binding.linearLayoutUsername.getHeight();
        binding.imageViewCaptcha.setLayoutParams(params);
        binding.imageViewCaptcha.setImageBitmap(captchaResult);
    }

    public LoginViewModel getLogin() {
        return login;
    }

    private void checkReadPhoneStatePermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(requireContext(), CALL_PHONE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(requireContext(), READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            askReadPhoneStatusPermission();
        } else if (enableGpsForResult(requireActivity())) {
            initialize();
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
                .setPermissions(READ_PHONE_STATE, CALL_PHONE, ACCESS_FINE_LOCATION)
                .check();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == GPS_CODE) checkReadPhoneStatePermission();
        }
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.image_view_recaptcha) {
            createDNTCaptcha();
        } else if (id == R.id.button_login) {
            attempt(true);
        } else if (id == R.id.image_view_person) {
            new CustomDialogModel(Green, requireContext(), getSerial(requireActivity()),
                    R.string.serial, R.string.dear_user, R.string.accepted);
        } else if (id == R.id.image_view_password) {
            if (binding.editTextPassword.getInputType() != InputType.TYPE_CLASS_TEXT) {
                binding.editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT);
            } else
                binding.editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }
}