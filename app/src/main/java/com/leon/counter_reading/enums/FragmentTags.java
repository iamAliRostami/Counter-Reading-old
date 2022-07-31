package com.leon.counter_reading.enums;

public enum FragmentTags {

    TAKE_PHOTO("TAKE_PHOTO_");


    private final String value;

    FragmentTags(final String newValue) {
        value = newValue;
    }

    public String getValue() {
        return value;
    }
}
