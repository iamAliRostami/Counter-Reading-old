package com.leon.counter_reading.enums;

public enum FragmentTags {

    KARBARI("KARBARI"),
    TAVIZ("TAVIZ"),
    AHAD("AHAD"),
    SEARCH("SEARCH"),
    NAVIGATION("NAVIGATION"),
    TAKE_PHOTO("TAKE_PHOTO_"),
    ARE_YOU_SURE("ARE_YOU_SURE_"),
    REPORT_FORBID("REPORT_FORBID"),
    COUNTER_PLACE("COUNTER_PLACE"),
    SERIAL_DIALOG("SERIAL_DIALOG_"),
    READING_REPORT("READING_REPORT"),
    CALL_USER("CALL_USER"),
    POSSIBLE_DIALOG("POSSIBLE_DIALOG_");


    private final String value;

    FragmentTags(final String newValue) {
        value = newValue;
    }

    public String getValue() {
        return value;
    }
}
