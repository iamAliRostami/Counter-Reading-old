package com.leon.counter_reading.utils.backup_restore.model;

import com.leon.counter_reading.tables.QotrDictionary;

public class QotrDictionaryTemp {
    public int id;
    public String title;
    public int isSelected;

    public QotrDictionary getQotrDictionary() {
        final QotrDictionary qotrDictionary = new QotrDictionary();
        qotrDictionary.id = id;
        qotrDictionary.isSelected = isSelected == 1;
        qotrDictionary.title = title;
        return qotrDictionary;
    }
}