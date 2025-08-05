package com.example.mobao.data.repository

import android.content.Context
import com.example.mobao.data.model.DatabaseProvider
import com.example.mobao.data.model.Medicine
import com.example.mobao.util.AlarmHelper
import com.google.gson.Gson
import java.time.LocalTime
import java.util.Calendar

class MedicineRepository(private val context: Context) {

    suspend fun insertMedicineWithTimes(name: String, times: List<LocalTime>, count: Int?) {
        val db = DatabaseProvider.getDatabase(context)
        val dao = db.medicineDao()

        val jsonTimes = Gson().toJson(times)
        val medicine = Medicine(
            name = name,
            reminderTimesJson = jsonTimes,
            totalPillCount = count,
            remainingPillCount = count
        )

        val id = dao.insertMedicine(medicine).toInt()

        // 선택된 모든 시간에 대해 알람 등록
        times.forEachIndexed { index, time ->
            val reminderTimeInMillis = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, time.hour)
                set(Calendar.MINUTE, time.minute)
                set(Calendar.SECOND, 0)
            }.timeInMillis

            // 고유 requestCode = id*100 + index
            AlarmHelper.scheduleReminder(context, id * 100 + index, reminderTimeInMillis, 24 * 60 * 60 * 1000L)
        }
    }

    suspend fun getMedicineById(id: Int): Medicine? {
        return DatabaseProvider.getDatabase(context).medicineDao().getMedicineById(id)
    }
}
