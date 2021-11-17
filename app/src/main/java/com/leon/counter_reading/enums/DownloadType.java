package com.leon.counter_reading.enums;

public enum DownloadType {
    NORMAL(0),
    RETRY(1),
    OFFLINE(2),
    SPECIAL(3);

    private final int value;
    DownloadType(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }
}
