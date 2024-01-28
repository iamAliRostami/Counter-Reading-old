package com.leon.counter_reading.enums;

public enum HighLowStateEnum {
    NORMAL(1),
    LOW(2),
    HIGH(3),
    ZERO(4),
    UN_CALCULATED(5);

    private final int value;

    HighLowStateEnum(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }
}
