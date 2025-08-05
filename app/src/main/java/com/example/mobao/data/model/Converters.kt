package com.example.mobao.data.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalTime

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromLocalTimeList(times: List<LocalTime>): String {
        return gson.toJson(times)
    }

    @TypeConverter
    fun toLocalTimeList(data: String): List<LocalTime> {
        val type = object : TypeToken<List<LocalTime>>() {}.type
        return gson.fromJson(data, type)
    }
}
