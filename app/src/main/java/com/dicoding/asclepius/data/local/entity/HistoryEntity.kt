package com.dicoding.asclepius.data.local.entity

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "imageUri")
    val imageUri: Uri,
    @ColumnInfo(name = "prediction")
    val prediction: String
)
