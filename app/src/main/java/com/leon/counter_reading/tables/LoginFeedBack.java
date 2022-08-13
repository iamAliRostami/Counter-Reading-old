package com.leon.counter_reading.tables;

import com.google.gson.annotations.SerializedName;

public class LoginFeedBack {
    @SerializedName("access_token")
    public final String accessToken;
    public final String refresh_token;
    public String displayName;
    public String userCode;
    public String XSRFToken;
    public String message;
    public int status;
    public boolean isValid;

    public LoginFeedBack(String access_token, String refresh_token) {
        this.accessToken = access_token;
        this.refresh_token = refresh_token;
    }
}
