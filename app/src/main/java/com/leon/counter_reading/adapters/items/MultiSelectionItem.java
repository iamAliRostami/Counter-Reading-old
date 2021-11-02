package com.leon.counter_reading.adapters.items;

public class MultiSelectionItem {
    private final String name;
    private final Boolean value;

    public MultiSelectionItem(String name, Boolean value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Boolean getValue() {
        return value;
    }
}