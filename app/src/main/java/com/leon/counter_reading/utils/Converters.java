package com.leon.counter_reading.utils;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Converters {
    @TypeConverter
    public static ArrayList<String> fromString(String value) {
        Type listType = new TypeToken<ArrayList<String>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<String> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    public static String replaceNonstandardDigits(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            final char ch = input.charAt(i);
            if (isNonstandardDigit(ch)) {
                final int numericValue = Character.getNumericValue(ch);
                if (numericValue >= 0) builder.append(numericValue);
            } else builder.append(ch);
        }
        return builder.toString();
    }

    private static boolean isNonstandardDigit(char ch) {
        return Character.isDigit(ch) && !(ch >= '0' && ch <= '9');
    }
}
