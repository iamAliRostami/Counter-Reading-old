package com.leon.counter_reading.enums;

public enum BundleEnum {
    BILL_ID("bill_Id"),
    IMAGE("image"),
    THEME("theme"),
    ON_OFF_LOAD("on_off_load"),
    COUNTER_STATE_POSITION("counter_state_position"),
    COUNTER_STATE_CODE("counter_state_code"),
    TRACKING("tracking"),
    POSITION("position"),
    NUMBER("number"),
    UNREAD("total"),
    TOTAL("unread"),
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
    TYPE("type");

    private final String value;

    BundleEnum(final String newValue) {
        value = newValue;
    }

    public String getValue() {
        return value;
    }
}
