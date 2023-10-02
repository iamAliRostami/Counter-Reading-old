package com.leon.counter_reading.view_models;

import static com.leon.counter_reading.enums.SharedReferenceKeys.PASSWORD;
import static com.leon.counter_reading.enums.SharedReferenceKeys.USERNAME;
import static com.leon.counter_reading.helpers.MyApplication.getAndroidVersion;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.utils.Crypto.decrypt;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.google.gson.annotations.SerializedName;
import com.leon.counter_reading.BR;
import com.leon.counter_reading.BuildConfig;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;
import com.leon.counter_reading.utils.CalendarTool;

import java.text.MessageFormat;

public class LoginViewModel extends BaseObservable {
    @SerializedName("access_token")
    private String accessToken;
    @SerializedName("refresh_token")
    private String refreshToken;
    private String username;
    private String password;
    private transient String version;
    private transient String oldUsername;
    private transient String oldPassword;
    private transient boolean saved;
    private int status;
    private String deviceSerial;
    private String appVersion;
    private String displayName;
    private String userCode;
    private String XSRFToken;
    private String message;
    private boolean isValid;
    private String data;
    private String dntCaptchaImgUrl;
    private String dntCaptchaId;
    private String dntCaptchaTextValue;
    private String dntCaptchaTokenValue;
    private String dntCaptchaText;
    private String dntCaptchaToken;
    private String dntCaptchaInputText;

    public LoginViewModel(String deviceSerial) {
        setAppVersion(BuildConfig.VERSION_NAME);
        setSaved(true);
        setDeviceSerial(deviceSerial);
        setVersion(String.format("%s *** %s", getAndroidVersion(), BuildConfig.VERSION_NAME));
        final ISharedPreferenceManager sharedPreferenceManager = getApplicationComponent().SharedPreferenceModel();
        if (sharedPreferenceManager.checkIsNotEmpty(USERNAME.getValue()) &&
                sharedPreferenceManager.checkIsNotEmpty(PASSWORD.getValue())) {
            setUsername(sharedPreferenceManager.getStringData(USERNAME.getValue()));
            setPassword(decrypt(sharedPreferenceManager.getStringData(PASSWORD.getValue())));
        }
    }

    @Bindable
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        notifyPropertyChanged(BR.username);
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
    }

    public String getOldUsername() {
        return oldUsername;
    }

    public void setOldUsername(String oldUsername) {
        this.oldUsername = oldUsername;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getDeviceSerial() {
        return deviceSerial;
    }

    public void setDeviceSerial(String deviceSerial) {
        this.deviceSerial = deviceSerial;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    @Bindable
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        notifyPropertyChanged(BR.accessToken);
    }

    @Bindable
    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        notifyPropertyChanged(BR.refreshToken);
    }

    @Bindable
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        notifyPropertyChanged(BR.displayName);
    }

    @Bindable
    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
        notifyPropertyChanged(BR.userCode);
    }

    @Bindable
    public String getXSRFToken() {
        return XSRFToken;
    }

    public void setXSRFToken(String XSRFToken) {
        this.XSRFToken = XSRFToken;
        notifyPropertyChanged(BR.xSRFToken);
    }

    @Bindable
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        notifyPropertyChanged(BR.message);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    @Bindable
    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
        notifyPropertyChanged(BR.saved);
    }

    @Bindable
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
        notifyPropertyChanged(BR.version);
    }

    public boolean setLoginFeedback(LoginViewModel loginFeedback) {
        if (loginFeedback == null)
            return true;
        setAccessToken(loginFeedback.getAccessToken());
        setRefreshToken(loginFeedback.getRefreshToken());
        setUserCode(loginFeedback.getUserCode());
        setXSRFToken(loginFeedback.getXSRFToken());
        setMessage(loginFeedback.getMessage());
        setStatus(loginFeedback.getStatus());
        setValid(loginFeedback.isValid());
        return false;
    }

    public boolean checkUserPassword() {
        return getApplicationComponent().SharedPreferenceModel().getStringData(USERNAME.getValue()).equals(username) &&
                decrypt(getApplicationComponent().SharedPreferenceModel().getStringData(PASSWORD.getValue())).equals(password);
    }

    public boolean checkUserPasswordChange() {
        return !username.equals(getOldUsername()) && !password.equals(getOldPassword());
    }

    private void validation() {
        final boolean isUsernameValid = !TextUtils.isEmpty(getUsername());
        final boolean isPasswordValid = !TextUtils.isEmpty(getPassword());
        setValid(isUsernameValid && isPasswordValid);
    }

    public TextWatcher usernameWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setUsername(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                validation();
            }
        };
    }

    public TextWatcher passwordWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setPassword(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                validation();
            }
        };
    }

    @Bindable
    public String getDntCaptchaInputText() {
        return dntCaptchaInputText;
    }

    public void setDntCaptchaInputText(String dntCaptchaInputText) {
        this.dntCaptchaInputText = dntCaptchaInputText;
        notifyPropertyChanged(BR.dntCaptchaInputText);
    }

    public String getDntCaptchaImgUrl() {
        return dntCaptchaImgUrl;
    }

    public void setDntCaptchaImgUrl(String dntCaptchaImgUrl) {
        this.dntCaptchaImgUrl = dntCaptchaImgUrl;
    }

    public String getDntCaptchaId() {
        return dntCaptchaId;
    }

    public void setDntCaptchaId(String dntCaptchaId) {
        this.dntCaptchaId = dntCaptchaId;
    }

    public String getDntCaptchaTextValue() {
        return dntCaptchaTextValue;
    }

    public void setDntCaptchaTextValue(String dntCaptchaTextValue) {
        this.dntCaptchaTextValue = dntCaptchaTextValue;
    }

    public String getDntCaptchaTokenValue() {
        return dntCaptchaTokenValue;
    }

    public void setDntCaptchaTokenValue(String dntCaptchaTokenValue) {
        this.dntCaptchaTokenValue = dntCaptchaTokenValue;
    }

    public String getDntCaptchaText() {
        return dntCaptchaText;
    }

    public void setDntCaptchaText(String dntCaptchaText) {
        this.dntCaptchaText = dntCaptchaText;
    }

    public String getDntCaptchaToken() {
        return dntCaptchaToken;
    }

    public void setDntCaptchaToken(String dntCaptchaToken) {
        this.dntCaptchaToken = dntCaptchaToken;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Bindable
    public String getDate() {
        return MessageFormat.format("امروز {0}، {1}", new CalendarTool().getIranianWeekDayStr(),
                new CalendarTool().getIranianDate());
    }
}
