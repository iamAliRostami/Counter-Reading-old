package com.leon.counter_reading.tables;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "DynamicTraverse", indices = @Index(value = {"customId"}, unique = true))
public class DynamicTraverse {
    @PrimaryKey(autoGenerate = true)
    public int customId;
    public int id;
    public String storageTitle;
    public String title;
    public boolean isChangeable;
    public boolean defaultValue;
    public boolean isActive;
}
