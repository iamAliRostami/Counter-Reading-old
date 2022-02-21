package com.leon.counter_reading.tables;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface OffLoadReportDao {
    @Query("SELECT * FROM OffLoadReport WHERE isSent = :isSent")
    List<OffLoadReport> getAllOffLoadReport(boolean isSent);

    @Query("SELECT * FROM OffLoadReport WHERE onOffLoadId = :id AND trackNumber = :trackNumber")
    List<OffLoadReport> getAllOffLoadReportById(String id, int trackNumber);

    @Query("SELECT * FROM OffLoadReport " +
            "Inner Join OnOffLoadDto On OnOffLoadDto.id = OffLoadreport.onOffLoadId " +
            "Inner Join TrackingDto On OnOffLoadDto.trackNumber = TrackingDto.trackNumber " +
            "WHERE TrackingDto.isActive = :isActive AND isSent = :isSent")
    List<OffLoadReport> getAllOffLoadReportByActive(boolean isActive, boolean isSent);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateOffLoadReport(List<OffLoadReport> offLoadReports);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOffLoadReport(OffLoadReport offLoadReport);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOffLoadReport(ArrayList<OffLoadReport> offLoadReport);

    @Query("DELETE FROM OffLoadReport WHERE reportId = :reportId AND onOffLoadId = :onOffLoadId AND trackNumber = :trackNumber")
    void deleteOffLoadReport(int reportId, int trackNumber, String onOffLoadId);

    @Query("UPDATE OffLoadReport SET isSent = :isSent")
    void updateOffLoadReportByIsSent(boolean isSent);
}
