package com.dicoding.asclepius.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.asclepius.data.local.entity.HistoryEntity

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun saveResult(result: HistoryEntity): Long

    @Query("SELECT * FROM HistoryEntity WHERE imageUri = :imageUri LIMIT 1")
    fun getResultByImageUri(imageUri: String): HistoryEntity?

    @Query("SELECT * FROM HistoryEntity")
    fun showResult() : LiveData<List<HistoryEntity>>

    @Delete
    fun deleteResult(result: HistoryEntity)
}