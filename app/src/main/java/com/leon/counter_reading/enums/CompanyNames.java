package com.leon.counter_reading.enums;

public enum CompanyNames {
    TEH_TOTAL(0),
    ZONE1(1),
    ZONE2(2),
    ZONE3(3),
    ZONE4(4),
    ZONE5(5),
    ZONE6(6),
    TSW(7),
    TE(8),
    TSE(9),
    TW(10),
    ESF(11),
    DEBUG(12),
    ESF_MAP(13),
    KSH(14),
    INDEPENDENT(15);
    private final int value;

    CompanyNames(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }

}
