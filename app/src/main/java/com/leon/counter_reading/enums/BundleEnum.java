package com.leon.counter_reading.enums;

public enum BundleEnum {
    BILL_ID("bill_Id"),
    COMPLETELY_DELETE("completely_delete"),
    IMAGE("image"),
    COUNTER_HAS_IMAGE("counter_has_image"),
    REPORT_HAS_IMAGE("report_has_image"),
    TRACK_HAS_IMAGE("track_has_image"),
    THEME("theme"),
    ON_OFF_LOAD("on_off_load"),
    JUST_MOBILE("just_mobile"),
    COUNTER_STATE_POSITION("counter_state_position"),
    COUNTER_STATE_CODE("counter_state_code"),
    TRACKING("tracking"),
    POSITION("position"),
    NUMBER("number"),
    NAME("name"),
    CONTINUE("continue"),
    UNREAD("unread"),
    BEFORE_READ("before_read"),
    READ("read"),
    TOTAL("total"),
    ZERO("zero"),
    HIGH("high"),
    LOW("low"),
    IS_MANE("is_mane"),
    NORMAL("normal"),
    READ_STATUS("read_status"),
    ZONE_ID("zone_id"),
    SENT("sent"),
    DESCRIPTION("description"),
    LATITUDE("latitude"),
    LONGITUDE("longitude"),

    TOTAL_REPORTS("all_report"),
    SENT_REPORT("sent_report"),
    UNSENT_REPORT("unsent_report"),
    ACTIVATION_REPORT("activation_report"),
    FORBIDDEN_REPORT("forbidden_report"),
    TYPE("type");

    private final String value;

    BundleEnum(final String newValue) {
        value = newValue;
    }

    public String getValue() {
        return value;
    }
}
