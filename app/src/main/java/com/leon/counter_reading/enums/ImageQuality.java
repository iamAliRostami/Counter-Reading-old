package com.leon.counter_reading.enums;

public enum ImageQuality {
    HIGH(1),
    MEDIUM(2),
    LOW(3);
    private final int value;

    ImageQuality(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }
}
