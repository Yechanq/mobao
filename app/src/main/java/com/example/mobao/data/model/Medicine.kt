package com.example.mobao.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicines")
data class Medicine(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val reminderTimesJson: String,
    val totalPillCount: Int?,
    val remainingPillCount: Int?,
    val createdAt: Long = System.currentTimeMillis()
)


