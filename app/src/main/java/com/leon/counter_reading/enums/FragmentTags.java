package com.leon.counter_reading.enums;

public enum FragmentTags {

    TAKE_PHOTO("TAKE_PHOTO_"),
    COUNTER_PLACE("COUNTER_PLACE"),
    NAVIGATION("NAVIGATION"),
    SEARCH("SEARCH"),
    READING_REPORT("READING_REPORT"),
    POSSIBLE_DIALOG("POSSIBLE_DIALOG_"),
    ARE_YOU_SURE("ARE_YOU_SURE_"),
    REPORT_FORBID("REPORT_FORBID");


    private final String value;

    FragmentTags(final String newValue) {
        value = newValue;
    }

    public String getValue() {
        return value;
    }
}
