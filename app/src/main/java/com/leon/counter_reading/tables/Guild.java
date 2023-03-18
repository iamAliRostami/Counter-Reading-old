package com.leon.counter_reading.tables;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "Guild", indices = @Index(value = {"customId"}, unique = true))
public class Guild {
    @PrimaryKey(autoGenerate = true)
    public int customId;
    public int id;
    public String title;
}
