package com.leon.counter_reading.tables;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import okhttp3.MultipartBody;

@Entity(tableName = "Image", indices = @Index(value = "id", unique = true))
public class Image {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int trackNumber;
    public long size;
    public String OnOffLoadId;
    public String Description;
    public String address;
    public boolean isSent;
    public boolean isDeleted;
    public boolean isArchived;
//    @Ignore
    public boolean isGallery;
    @Ignore
    public MultipartBody.Part File;
}