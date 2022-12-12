package com.leon.counter_reading.tables;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface TrackingDao {
    @Query("Select * From TrackingDto")
    List<TrackingDto> getTrackingDto();

    @Query("Select * From TrackingDto WHERE isArchive = :isArchive")
    List<TrackingDto> getTrackingDtoNotArchive(boolean isArchive);

    @Query("Select * From TrackingDto WHERE isArchive = :isArchive AND Date(archiveDateTime) <  Date(:date)")
    List<TrackingDto> getTrackingDtoNotArchive(boolean isArchive, String date);

    @Query("Select * From TrackingDto WHERE isArchive = :isArchive AND isActive = :isActive")
    List<TrackingDto> getTrackingDtosIsActiveNotArchive(boolean isActive, boolean isArchive);

    @Query("Select trackNumber From TrackingDto WHERE isArchive = :isArchive AND " +
            "Date(archiveDateTime) <  Date(:expiredDate)")
    List<Integer> getTrackingDtosExpired(boolean isArchive, String expiredDate);

    @Query("Select zoneId From TrackingDto WHERE isArchive = :isArchive AND isActive = :isActive")
    List<Integer> getZoneIdIsActiveNotArchive(boolean isActive, boolean isArchive);

    @Query("Select alalHesabPercent From TrackingDto Where zoneId = :zoneId")
    int getAlalHesabByZoneId(int zoneId);

    @Query("Select alalHesabPercent From TrackingDto Where trackNumber = :trackNumber")
    int getAlalHesabByTrackNumber(int trackNumber);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTrackingDto(TrackingDto trackingDto);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllTrackingDtos(ArrayList<TrackingDto> trackingDtos);

    @Query("Update TrackingDto Set isActive = :isActive Where id = :id AND isArchive = 0")
    void updateTrackingDtoByStatus(String id, boolean isActive);

    @Query("Update TrackingDto Set isLocked = :isLocked Where trackNumber = :trackNumber")
    void updateTrackingDtoByLock(int trackNumber, boolean isLocked);

    //update on upload
    @Query("Update TrackingDto Set isArchive = :isArchive, isActive = :isActive," +
            " archiveDateTime = :archiveDateTime  Where id = :id")
    void updateTrackingDtoByArchive(String id, boolean isArchive, boolean isActive, String archiveDateTime);

    //update on delete
    @Query("Update TrackingDto Set isArchive = :isArchive, isActive = :isActive, " +
            "isDeleted = :isDeleted, archiveDateTime = :archiveDateTime Where id = :id")
    void updateTrackingDtoByArchive(String id, boolean isArchive, boolean isActive,
                                    boolean isDeleted, String archiveDateTime);

    //update on delete
    @Query("Update TrackingDto Set isArchive = :isArchive, isActive = :isActive, " +
            "isDeleted = :isDeleted, archiveDateTime = :archiveDateTime")
    void updateTrackingDtoByArchive(boolean isArchive, boolean isActive, boolean isDeleted,
                                    String archiveDateTime);

    @Query("DELETE FROM TrackingDto WHERE trackNumber in(:trackNumbers)")
    void deleteTrackingDtos(List<Integer> trackNumbers);

    @Query("DELETE FROM TrackingDto WHERE id = :id")
    void deleteTrackingDtosCompletely(String id);

    @Query("DELETE FROM TrackingDto")
    void deleteTrackingDtosCompletely();

    @Query("DELETE FROM TrackingDto WHERE trackNumber = :trackNumber AND isArchive = :isArchive")
    void deleteTrackingDto(int trackNumber, boolean isArchive);

    @Query("SELECT COUNT(*) FROM TrackingDto WHERE isActive = :isActive AND isArchive = :isArchive")
    int getTrackingDtoActivesCount(boolean isActive, boolean isArchive);

    @Query("SELECT COUNT(*) FROM TrackingDto WHERE trackNumber= :trackNumber")
    int getTrackingDtoActivesCountByTracking(int trackNumber);

    @Query("SELECT COUNT(*) FROM TrackingDto WHERE trackNumber= :trackNumber AND isArchive = :isArchive")
    int getTrackingDtoArchiveCountByTrackNumber(int trackNumber, boolean isArchive);
}
