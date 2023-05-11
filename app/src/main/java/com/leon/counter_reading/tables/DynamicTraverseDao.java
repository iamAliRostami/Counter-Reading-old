package com.leon.counter_reading.tables;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DynamicTraverseDao {
    @Query("Select * From DynamicTraverse")
    List<DynamicTraverse> getDynamicTraverses();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDynamicTraverse(List<DynamicTraverse> dynamicTraverses);

    @Query("DELETE FROM DynamicTraverse")
    void deleteDynamicTraverse();
}
