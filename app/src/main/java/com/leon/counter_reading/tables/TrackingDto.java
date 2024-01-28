package com.leon.counter_reading.tables;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

@Entity(tableName = "TrackingDto", indices = @Index(value = {"customId"/*,"id","trackNumber"*/}, unique = true))
public class TrackingDto {
    @PrimaryKey(autoGenerate = true)
    public int customId;
    public String id;
    public int trackNumber;
    public String listNumber;
    public String insertDateJalali;
    public int zoneId;
    public String zoneTitle;
    public boolean isBazdid;
    public int year;
    public boolean isRoosta;
    public String fromEshterak;
    public String toEshterak;
    public String fromDate;
    public String toDate;
    public String archiveDateTime;
    public int itemQuantity;
    public int alalHesabPercent;
    public int imagePercent;
    public boolean hasPreNumber;
    public boolean hasImage;
    public boolean displayBillId;
    //TODO
    public boolean displayDebt;
    public boolean displayRadif;
    public boolean displayMobile;
    public boolean displayPreDate;

    public boolean isActive;
    public boolean isArchive;
    public boolean isLocked;
    public boolean isDeleted;
    public boolean isRepeat;

    public String x;
    public String y;

    public static ArrayList<String> getTrackingDtoItems(ArrayList<TrackingDto> trackingDtos) {
        final ArrayList<String> items = new ArrayList<>();
        for (TrackingDto trackingDto : trackingDtos) {
            items.add(String.valueOf(trackingDto.trackNumber));
        }
        return items;
    }

    public static String[] getTrackingDtoItems(ArrayList<TrackingDto> trackingDtos, String last) {
        final String[] items = new String[trackingDtos.size() + 1];
        items[0] = last;
        for (int i = 0; i < trackingDtos.size(); i++) {
            items[i + 1] = String.valueOf(trackingDtos.get(i).trackNumber);
        }
        return items;
    }
}