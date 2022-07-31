package com.leon.counter_reading.utils;

import android.util.Base64;

public class Crypto {

    public static String encrypt(String password) {
        password = Base64.encodeToString(password.getBytes(), Base64.DEFAULT);
        return Base64.encodeToString(password.getBytes(), Base64.NO_CLOSE);
    }

    public static String decrypt(String encodedPassword) {
        encodedPassword = new String(Base64.decode(encodedPassword, Base64.DEFAULT));
        return new String(Base64.decode(encodedPassword, Base64.DEFAULT));
    }
}
