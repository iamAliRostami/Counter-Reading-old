package com.leon.counter_reading.tables;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GuildsDao {
    @Query("SELECT * FROM Guilds")
    List<Guilds> getAllGuilds();

    @Query("SELECT * FROM Guilds WHERE id = :id")
    List<Guilds> getGuildById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGuild(Guilds guilds);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGuild(List<Guilds> guilds);

    @Query("DELETE FROM Guilds WHERE id = :id")
    void deleteGuild(int id);

    @Query("DELETE FROM Guilds")
    void deleteGuildCompletely();
}
