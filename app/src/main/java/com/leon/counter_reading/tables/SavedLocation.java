package com.leon.counter_reading.tables;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "SavedLocation")
public class SavedLocation {
    public final double accuracy;
    public final double longitude;
    public final double latitude;
    @PrimaryKey(autoGenerate = true)
    public int id;

    public SavedLocation(double accuracy, double longitude, double latitude) {
        this.accuracy = accuracy;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public record LocationOnMap(double longitude, double latitude) {
    }
}
