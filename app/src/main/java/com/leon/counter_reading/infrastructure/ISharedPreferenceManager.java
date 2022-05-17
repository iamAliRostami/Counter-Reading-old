package com.leon.counter_reading.infrastructure;

public interface ISharedPreferenceManager {
    void putData(String key, String value);

    void putData(String key, int value);

    void putData(String key, boolean value);

    String getStringData(String key);

    int getIntData(String key);

    boolean getBoolData(String key);

    boolean checkIsNotEmpty(String key);

    boolean checkIsNotEmpty(String key, boolean b);
}
