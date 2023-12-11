package com.leon.counter_reading.tables;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "Guilds", indices = @Index(value = {"customId"}, unique = true))
public class Guilds {
    @PrimaryKey(autoGenerate = true)
    public int customId;
    public int id;
    public int moshtarakinId;
    public String title;
}
