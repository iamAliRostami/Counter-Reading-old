package com.leon.counter_reading.tables;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "OffLoadReport", indices = @Index(value = "customId", unique = true))
public class OffLoadReport {
    @PrimaryKey(autoGenerate = true)
    public int customId;
    public String onOffLoadId;
    public int reportId;
    public boolean isSent;
    public int trackNumber;
    public boolean hasImage;

    public OffLoadReport() {
    }

    public OffLoadReport(String onOffLoadId, int trackNumber, int reportId, boolean hasImage) {
        this.onOffLoadId = onOffLoadId;
        this.reportId = reportId;
        this.trackNumber = trackNumber;
        this.hasImage = hasImage;
    }
}
