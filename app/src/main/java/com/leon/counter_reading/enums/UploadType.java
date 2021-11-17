package com.leon.counter_reading.enums;

public enum UploadType {
    NORMAL(0),
    OFFLINE(1),
    MULTIMEDIA(2);
    private final int value;

    UploadType(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }
}
