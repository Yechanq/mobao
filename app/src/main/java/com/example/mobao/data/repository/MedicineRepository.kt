package com.example.mobao.data.repository

import android.content.Context
import com.example.mobao.data.model.DatabaseProvider
import com.example.mobao.data.model.Medicine
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import java.time.LocalTime
import com.google.gson.Gson
import com.example.mobao.util.AlarmHelper
import java.util.Calendar

class MedicineRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dao = DatabaseProvider.getDatabase(context).medicineDao()

    fun getAllMedicinesFlow(): Flow<List<Medicine>> = dao.getAllMedicinesFlow()

    suspend fun insertMedicineWithTimes(name: String, times: List<LocalTime>, count: Int?) {
        val jsonTimes = Gson().toJson(times)
        val medicine = Medicine(
            name = name,
            reminderTimesJson = jsonTimes,
            totalPillCount = count,
            remainingPillCount = count
        )

        val id = dao.insertMedicine(medicine).toInt()

        // 알림 등록
        times.forEachIndexed { index, time ->
            val reminderTimeInMillis = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, time.hour)
                set(Calendar.MINUTE, time.minute)
                set(Calendar.SECOND, 0)
            }.timeInMillis
            AlarmHelper.scheduleReminder(context, id * 100 + index, reminderTimeInMillis, 24 * 60 * 60 * 1000L)
        }
    }

    suspend fun updateMedicine(medicine: Medicine) = dao.updateMedicine(medicine)

    suspend fun deleteMedicine(medicine: Medicine) = dao.deleteMedicine(medicine)
}
