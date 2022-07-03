package com.leon.counter_reading.tables;

public class LoginInfo {
    public final String username;
    public final String password;
    public final String deviceSerial;
    public final String appVersion;

    public LoginInfo(String username, String password, String deviceSerial, String appVersion) {
        this.username = username;
        this.password = password;
        this.deviceSerial = deviceSerial;
        this.appVersion = appVersion;
    }
}