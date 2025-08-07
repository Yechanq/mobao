package com.example.mobao.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.time.LocalTime

@Entity(tableName = "medicines")
@TypeConverters(Converters::class)
data class Medicine(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val reminderTimes: List<LocalTime>,
    val totalPillCount: Int?,
    val remainingPillCount: Int?,
    val createdAt: Long = System.currentTimeMillis()
)


