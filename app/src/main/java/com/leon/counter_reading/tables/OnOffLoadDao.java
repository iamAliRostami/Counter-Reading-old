package com.leon.counter_reading.tables;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface OnOffLoadDao {
    @Query("select * From OnOffLoadDto WHERE id = :id AND trackingId = :trackingId ORDER BY eshterak")
    OnOffLoadDto getAllOnOffLoadById(String id, String trackingId);

    @Query("select * From OnOffLoadDto Where trackingId = :trackingId ORDER BY eshterak")
    List<OnOffLoadDto> getAllOnOffLoadByTracking(String trackingId);

    @Query("select * From OnOffLoadDto Where trackingId = :trackingId AND highLowStateId = :highLow ORDER BY eshterak")
    List<OnOffLoadDto> getAllOnOffLoadByHighLowAndTracking(String trackingId, int highLow);

    @Query("select * From OnOffLoadDto WHERE offLoadStateId = :offLoadStateId AND trackingId = :trackingId ORDER BY eshterak")
    List<OnOffLoadDto> getAllOnOffLoadRead(int offLoadStateId, String trackingId);

    @Query("select * From OnOffLoadDto WHERE trackingId = :trackingId AND " +
            "((counterStateId in (:counterStateId) AND hazf = 0) OR (offLoadStateId = :offLoadStateId)) " +
            "ORDER BY eshterak")
    List<OnOffLoadDto> getOnOffLoadReadByIsManeNotRead(List<Integer> counterStateId, int offLoadStateId, String trackingId);

    @Query("select * From OnOffLoadDto WHERE offLoadStateId = :offLoadStateId AND trackingId = :trackingId ORDER BY eshterak")
    List<OnOffLoadDto> getAllOnOffLoadNotRead(int offLoadStateId, String trackingId);//TODO

    @Query("select * From OnOffLoadDto WHERE trackingId = :trackingId AND offLoadStateId = :offLoadStateId")
    List<OnOffLoadDto> getOnOffLoadReadByTrackingAndOffLoad(String trackingId, int offLoadStateId);

    @Query("select * From OnOffLoadDto WHERE trackingId = :trackingId LIMIT 1")
    OnOffLoadDto getOnOffLoadReadByTrackingAndOffLoad(String trackingId);

    //TODO
    @Query("select OnOffLoadDto.id, OnOffLoadDto.counterNumber, OnOffLoadDto.counterStateId, " +
            "OnOffLoadDto.possibleAddress, OnOffLoadDto.possibleCounterSerial, " +
            "OnOffLoadDto.possibleEshterak, OnOffLoadDto.possibleMobile, " +
            "OnOffLoadDto.possiblePhoneNumber, OnOffLoadDto.possibleAhadMaskooniOrAsli, " +
            "OnOffLoadDto.possibleAhadTejariOrFari, OnOffLoadDto.possibleAhadSaierOrAbBaha, " +
            "OnOffLoadDto.possibleEmpty, OnOffLoadDto.possibleKarbariCode, OnOffLoadDto.guildId," +
            "OnOffLoadDto.description, OnOffLoadDto.counterNumberShown, OnOffLoadDto.attemptCount, " +
            "OnOffLoadDto.isLocked, OnOffLoadDto.gisAccuracy, OnOffLoadDto.phoneDateTime, " +
            "OnOffLoadDto.locationDateTime, OnOffLoadDto.x , OnOffLoadDto.y, " +
            "OnOffLoadDto.d1, OnOffLoadDto.d2 From OnOffLoadDto " +
            "Inner JOIN TrackingDto on OnOffLoadDto.trackingId = TrackingDto.id " +
            "WHERE OnOffLoadDto.offLoadStateId = :offLoadStateId AND TrackingDto.isActive = :isActive")

//    @Query("select * From OnOffLoadDto " +
//            "Inner JOIN TrackingDto on OnOffLoadDto.trackingId = TrackingDto.id " +
//            "WHERE OnOffLoadDto.offLoadStateId = :offLoadStateId AND TrackingDto.isActive = :isActive")
    List<OnOffLoadDto.OffLoad> getAllOnOffLoadInsert(int offLoadStateId, boolean isActive);

    //TODO set limit
    @Query("select OnOffLoadDto.id, OnOffLoadDto.counterNumber, OnOffLoadDto.counterStateId, " +
            "OnOffLoadDto.possibleAddress, OnOffLoadDto.possibleCounterSerial, " +
            "OnOffLoadDto.possibleEshterak, OnOffLoadDto.possibleMobile, " +
            "OnOffLoadDto.possiblePhoneNumber, OnOffLoadDto.possibleAhadMaskooniOrAsli, " +
            "OnOffLoadDto.possibleAhadTejariOrFari, OnOffLoadDto.possibleAhadSaierOrAbBaha, " +
            "OnOffLoadDto.possibleEmpty, OnOffLoadDto.possibleKarbariCode, " +
            "OnOffLoadDto.description, OnOffLoadDto.counterNumberShown, OnOffLoadDto.attemptCount, " +
            "OnOffLoadDto.isLocked, OnOffLoadDto.gisAccuracy, OnOffLoadDto.phoneDateTime, " +
            "OnOffLoadDto.locationDateTime, OnOffLoadDto.x , OnOffLoadDto.y, " +
            "OnOffLoadDto.d1, OnOffLoadDto.d2 From OnOffLoadDto " +
            "Inner JOIN TrackingDto on OnOffLoadDto.trackingId = TrackingDto.id " +
            "WHERE OnOffLoadDto.offLoadStateId = :offLoadStateId AND TrackingDto.isActive = :isActive " +
            "LIMIT 500")
    List<OnOffLoadDto.OffLoad> getAllOnOffLoadTopInserted(int offLoadStateId, boolean isActive);

    @Query("select * From OnOffLoadDto WHERE counterStateId in (:counterStateId) AND hazf = 0 AND " +
            "trackingId = :trackingId  ORDER BY eshterak")
    List<OnOffLoadDto> getOnOffLoadReadByIsMane(List<Integer> counterStateId, String trackingId);

    @Query("select COUNT(*) From OnOffLoadDto WHERE trackingId = :trackingId AND highLowStateId =:highLowStateId")
    int getOnOffLoadReadCountByStatus(String trackingId, int highLowStateId);

    @Query("select COUNT(*) From OnOffLoadDto WHERE offLoadStateId == :offLoadStateId AND trackingId = :trackingId")
    int getOnOffLoadReadCount(int offLoadStateId, String trackingId);

    @Query("select COUNT(*) From OnOffLoadDto WHERE offLoadStateId = :offLoadStateId AND trackingId = :trackingId")
    int getOnOffLoadUnreadCount(int offLoadStateId, String trackingId);

    @Query("select COUNT(*) From OnOffLoadDto WHERE trackingId = :trackingId")
    int getOnOffLoadCount(String trackingId);

    @Query("select COUNT(*) From OnOffLoadDto WHERE counterStateId = :counterStateId AND " +
            "trackingId = :trackingId AND hazf = 0")
    int getOnOffLoadIsManeCount(int counterStateId, String trackingId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAllOnOffLoad(List<OnOffLoadDto> onOffLoadDtos);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateOnOffLoad(OnOffLoadDto onOffLoadDto);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateOnOffLoads(List<OnOffLoadDto> onOffLoadDtos);


    @Query("UPDATE OnOffLoadDto set offLoadStateId = :offLoadStateId WHERE id IN (:id)")
    void updateOnOffLoad(int offLoadStateId, String[] id);

    @Query("UPDATE OnOffLoadDto set offLoadStateId = :offLoadStateId WHERE id = :id")
    void updateOnOffLoadById(int offLoadStateId, String id);

    @Query("Update OnOffLoadDto set offLoadStateId = :offLoadStateId WHERE trackingId = :trackingId")
    void updateOnOffLoad(int offLoadStateId, String trackingId);

    @Query("UPDATE OnOffLoadDto set isBazdid = :isBazdid WHERE id = :id")
    void updateOnOffLoad(boolean isBazdid, String id);

    @Query("UPDATE OnOffLoadDto set possibleCounterSerial = :possibleCounterSerial WHERE id = :id")
    void updateOnOffLoad(String possibleCounterSerial, String id);

    @Query("UPDATE OnOffLoadDto set possibleKarbariCode = :possibleKarbariCode WHERE id = :id")
    void updateOnOffLoad(String id, int possibleKarbariCode);

    @Query("UPDATE OnOffLoadDto set possibleAhadMaskooniOrAsli = :possibleAhadMaskooniOrAsli, " +
            "possibleAhadTejariOrFari = :possibleAhadTejariOrFari WHERE id = :id")
    void updateOnOffLoad(int possibleAhadMaskooniOrAsli, int possibleAhadTejariOrFari, String id);

    @Query("UPDATE OnOffLoadDto set description = :description WHERE id = :id")
    void updateOnOffLoadDescription(String id, String description);

    @Query("UPDATE OnOffLoadDto set d1 = :d1, d2 = :d2 WHERE id = :id")
    void updateOnOffLoadLocation(String id, String d1, String d2);

    @Query("UPDATE OnOffLoadDto set possibleAddress = :address, possibleCounterSerial = :serialNumber," +
            " possibleMobile = :possibleMobile, possibleEshterak = :possibleEshterak," +
            " possiblePhoneNumber = :phoneNumber, possibleEmpty = :possibleEmpty WHERE id = :id")
    void updateOnOffLoad(String id, String possibleEshterak, String possibleMobile, Integer possibleEmpty,
                         String phoneNumber, String serialNumber, String address);

    @Query("UPDATE OnOffLoadDto set attemptCount = :attemptNumber WHERE id = :id")
    void updateOnOffLoadByAttemptNumber(String id, int attemptNumber);

    @Query("UPDATE OnOffLoadDto set isLocked = :isLocked WHERE trackingId = :trackingId")
    void updateOnOffLoadByLock(String trackingId, boolean isLocked);

    @Query("UPDATE OnOffLoadDto set isLocked = :isLocked WHERE id = :id AND trackingId = :trackingId")
    void updateOnOffLoadByLock(String id, String trackingId, boolean isLocked);

    @Query("DELETE FROM OnOffLoadDto WHERE trackNumber = :trackNumber")
    void deleteOnOffLoads(int trackNumber);

    @Query("DELETE FROM OnOffLoadDto WHERE trackNumber in (:trackNumbers)")
    void deleteOnOffLoads(List<Integer> trackNumbers);
}
