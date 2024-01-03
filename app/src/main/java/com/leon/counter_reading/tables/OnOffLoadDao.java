package com.leon.counter_reading.tables;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface OnOffLoadDao {
    @Query("select * From OnOffLoadDto WHERE  trackingId = :trackingId AND id = :id ORDER BY eshterak")
    OnOffLoadDto getAllOnOffLoadById(String id, String trackingId);

    @Query("select * From OnOffLoadDto Where trackingId = :trackingId ORDER BY eshterak")
    List<OnOffLoadDto> getAllOnOffLoadByTracking(String trackingId);

    @Query("select * From OnOffLoadDto Where trackingId = :trackingId AND highLowStateId = :highLow ORDER BY eshterak")
    List<OnOffLoadDto> getAllOnOffLoadByHighLowAndTracking(String trackingId, int highLow);

    @Query("select * From OnOffLoadDto WHERE  trackingId = :trackingId AND offLoadStateId = :offLoadStateId ORDER BY eshterak")
    List<OnOffLoadDto> getAllOnOffLoadRead(int offLoadStateId, String trackingId);

    @Query("select * From OnOffLoadDto WHERE trackingId = :trackingId AND " +
            "((counterStateId in (:counterStateId) AND hazf = 0) OR (offLoadStateId = :offLoadStateId)) " +
            "ORDER BY eshterak")
    List<OnOffLoadDto> getOnOffLoadReadByIsManeNotRead(List<Integer> counterStateId, int offLoadStateId, String trackingId);

    @Query("select * From OnOffLoadDto WHERE trackingId = :trackingId  AND offLoadStateId = :offLoadStateId ORDER BY eshterak")
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
    List<OnOffLoadDto.OffLoad> getAllOnOffLoadInsert(int offLoadStateId, boolean isActive);

    @Query("select OnOffLoadDto.id, OnOffLoadDto.counterNumber, OnOffLoadDto.counterStateId, " +
            "OnOffLoadDto.possibleAddress, OnOffLoadDto.possibleCounterSerial, " +
            "OnOffLoadDto.possibleEshterak, OnOffLoadDto.possibleMobile, " +
            "OnOffLoadDto.possiblePhoneNumber, OnOffLoadDto.possibleAhadMaskooniOrAsli, " +
            "OnOffLoadDto.possibleAhadTejariOrFari, OnOffLoadDto.possibleAhadSaierOrAbBaha, " +
            "OnOffLoadDto.possibleEmpty, OnOffLoadDto.possibleKarbariCode, " +
            "OnOffLoadDto.description, OnOffLoadDto.counterNumberShown, OnOffLoadDto.attemptCount, " +
            "OnOffLoadDto.isLocked, OnOffLoadDto.gisAccuracy, OnOffLoadDto.phoneDateTime, " +
            "OnOffLoadDto.locationDateTime, OnOffLoadDto.x , OnOffLoadDto.y, " +
            "OnOffLoadDto.d1, OnOffLoadDto.d2, OnOffLoadDto.offLoadStateId From OnOffLoadDto " +
            "Inner JOIN TrackingDto on OnOffLoadDto.trackingId = TrackingDto.id " +
            "WHERE OnOffLoadDto.offLoadStateId = :offLoadStateId AND TrackingDto.isActive = :isActive " +
            "LIMIT 500")
    List<OnOffLoadDto.OffLoad> getAllOnOffLoadTopInserted(int offLoadStateId, boolean isActive);

    @Query("select * From OnOffLoadDto WHERE trackingId = :trackingId AND counterStateId in (:counterStateId) " +
            "AND hazf = 0 ORDER BY eshterak")
    List<OnOffLoadDto> getOnOffLoadReadByIsMane(List<Integer> counterStateId, String trackingId);

    @Query("select * From OnOffLoadDto WHERE  trackingId = :trackingId AND counterStateId = :counterStateId")
    List<OnOffLoadDto> getOnOffLoadCounter(int counterStateId, String trackingId);


    @Query("select * From OnOffLoadDto WHERE  trackingId = :trackingId AND counterStateId != :counterStateId AND offLoadStateId == :offLoadStateId")
    List<OnOffLoadDto> getOnOffLoadCounterRead(int counterStateId, int offLoadStateId, String trackingId);

    @Query("select COUNT(*) From OnOffLoadDto WHERE trackingId = :trackingId AND highLowStateId =:highLowStateId")
    int getOnOffLoadReadCountByStatus(String trackingId, int highLowStateId);

    @Query("select COUNT(*) From OnOffLoadDto WHERE trackingId = :trackingId AND offLoadStateId == :offLoadStateId")
    int getOnOffLoadReadCount(int offLoadStateId, String trackingId);

    @Query("select COUNT(*) From OnOffLoadDto WHERE  trackingId = :trackingId AND counterStateId = :counterStateId")
    int getOnOffLoadUnreadCount(int counterStateId, String trackingId);

    @Query("select COUNT(*) From OnOffLoadDto WHERE trackingId = :trackingId AND ((offLoadStateId > 0) " +
            "OR ((counterStateId == 0) OR (counterStateId IN (:mavane))))")
    int getOnOffLoadCount(String trackingId, List<Integer> mavane);

    @Query("select COUNT(*) From OnOffLoadDto WHERE trackingId = :trackingId AND counterStateId > 0 " +
            "AND offLoadStateId == 0")
    int getOnOffLoadReadCount(String trackingId);

    @Query("select COUNT(*) From OnOffLoadDto WHERE trackingId = :trackingId")
    int getOnOffLoad(String trackingId);


    @Query("select COUNT(*) From OnOffLoadDto WHERE trackingId = :trackingId AND " +
            "counterStateId IN (:counterStateId) AND hazf = 0")
    int getOnOffLoadIsManeCount(List<Integer> counterStateId, String trackingId);

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

    @Query("UPDATE OnOffLoadDto set attemptCount = :attemptNumber WHERE id = :id AND trackingId = :trackingId")
    void updateOnOffLoadByAttemptNumber(String id, String trackingId, int attemptNumber);

    @Query("UPDATE OnOffLoadDto set isLocked = :isLocked WHERE id = :id AND trackingId = :trackingId")
    void updateOnOffLoadByLock(String id, String trackingId, boolean isLocked);

    @Query("UPDATE OnOffLoadDto set counterNumberShown = :isShown WHERE id = :id AND trackingId = :trackingId")
    void updateOnOffLoadByIsShown(String id, String trackingId, boolean isShown);

    @Query("DELETE FROM OnOffLoadDto WHERE trackNumber = :trackNumber")
    void deleteOnOffLoads(int trackNumber);

    @Query("DELETE FROM OnOffLoadDto WHERE trackNumber in (:trackNumbers)")
    void deleteOnOffLoads(List<Integer> trackNumbers);
}
