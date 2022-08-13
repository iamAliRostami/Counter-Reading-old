package com.leon.counter_reading.view_models;

import static com.leon.counter_reading.enums.SharedReferenceKeys.PASSWORD;
import static com.leon.counter_reading.enums.SharedReferenceKeys.USERNAME;
import static com.leon.counter_reading.helpers.MyApplication.getAndroidVersion;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.utils.Crypto.decrypt;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.google.gson.annotations.SerializedName;
import com.leon.counter_reading.BR;
import com.leon.counter_reading.BuildConfig;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;

public class LoginViewModel extends BaseObservable {
    @SerializedName("access_token")
    private String accessToken;
    @SerializedName("access_token")
    private String refreshToken;
    private String username;
    private String password;
    private String deviceSerial;
    private String appVersion;
    private String version;
    private String displayName;
    private String userCode;
    private String XSRFToken;
    private String message;
    private boolean isValid;
    private boolean saved;
    private int status;

    public LoginViewModel(String version) {
        setVersion(version.concat(" ").concat(getAndroidVersion()).concat(" *** ")
                .concat(BuildConfig.VERSION_NAME));
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

    @Bindable
    public String getDeviceSerial() {
        return deviceSerial;
    }

    public void setDeviceSerial(String deviceSerial) {
        this.deviceSerial = deviceSerial;
        notifyPropertyChanged(BR.deviceSerial);
    }

    @Bindable
    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
        notifyPropertyChanged(BR.appVersion);
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

    @Bindable
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
        notifyPropertyChanged(BR.status);
    }

    @Bindable
    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
        notifyPropertyChanged(BR.valid);
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

    public boolean checkUserPassword() {
        return getApplicationComponent().SharedPreferenceModel().getStringData(USERNAME.getValue()).equals(username) &&
                decrypt(getApplicationComponent().SharedPreferenceModel().getStringData(PASSWORD.getValue())).equals(password);
    }
}
