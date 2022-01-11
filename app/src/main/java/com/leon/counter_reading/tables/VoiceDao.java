package com.leon.counter_reading.tables;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface VoiceDao {

    @Query("SELECT * FROM Voice WHERE OnOffLoadId = :OnOffLoadId")
    Voice getVoicesByOnOffLoadId(String OnOffLoadId);

    @Query("SELECT * FROM Voice WHERE isSent = :isSent")
    List<Voice> getVoicesByBySent(boolean isSent);

    @Query("SELECT * FROM Voice WHERE isSent = :isSent AND trackNumber = :trackNumber")
    List<Voice> getVoicesByBySentAndTrackNumber(int trackNumber, boolean isSent);

    @Query("SELECT COUNT(*) FROM Voice WHERE isSent = :isSent AND trackNumber = :trackNumber")
    int getUnsentVoiceCountByTrackNumber(int trackNumber, boolean isSent);

    @Query("SELECT COUNT(*) FROM Voice WHERE isSent = :isSent")
    int getUnsentVoiceCount(boolean isSent);

    @Query("SELECT SUM(size) FROM Voice WHERE isSent = :isSent")
    int getUnsentVoiceSizes(boolean isSent);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertVoice(Voice Voice);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateVoice(Voice Voice);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateVoice(List<Voice> Voice);

    @Query("DELETE FROM Voice WHERE id = :id")
    void deleteVoice(int id);
}
