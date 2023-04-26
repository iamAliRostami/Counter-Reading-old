package com.leon.counter_reading.enums;

public enum SharedReferenceKeys {
    READING_REPORT("reading_report"),
    ACCOUNT("account"),
    IMAGE("image"),
    MOBILE("mobile"),
    SERIAL("serial"),
    ADDRESS("address"),
    AHAD_EMPTY("ahad_empty"),
    AHAD_1("ahad_1"),
    AHAD_2("ahad_2"),
    AHAD_TOTAL("ahad_total"),
    KARBARI("karbari"),
    DESCRIPTION("description"),
    SHOW_AHAD_TITLE("show_ahad_title"),
    USERNAME_TEMP("username_temp"),
    PASSWORD_TEMP("password_temp"),
    USERNAME("username"),
    PASSWORD("password"),
    TOKEN("token"),
    REFRESH_TOKEN("refresh_token"),
    LOAD_USER_PASSWORD("load_user_password"),
    ANTIFORGERY("Antiforgery"),
    XSRF("xsrf"),
    USER_CODE("user_code"),
    DISPLAY_NAME("display_name"),
    THEME_STABLE("theme_stable"),
    DATE("date"),
    POINT("point"),
    SORT_TYPE("sort_type"),
    PERSONAL_CODE("personal_number"),
    AVATAR("avatar"),
    LAST_BACK_UP("last_back_up"),
    PROXY("proxy"),
    RTL_PAGING("paging_rtl"),
    KEYBOARD_TYPE("keyboard_type"),
    IMAGE_QUALITY("image_quality"),
    THEME_TEMPORARY("theme_temporary"),
    SORT_FILTER_PREF("SORT_FILTER_PREF"),
    SORT_ASC_KEY("SORT_ASC_KEY"),
    SORT_FILTER_KEY("SORT_FILTER_KEY"),
    GUILD("GUILD"),
    GUILD_FIRST("GUILD_FIRST");

    private final String value;

    SharedReferenceKeys(final String newValue) {
        value = newValue;
    }

    public String getValue() {
        return value;
    }
}
