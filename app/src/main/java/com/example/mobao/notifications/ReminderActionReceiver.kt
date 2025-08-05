package com.example.mobao.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.mobao.util.AlarmHelper
import android.app.NotificationManager
import com.example.mobao.data.model.DatabaseProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val medicineId = intent.getIntExtra("medicineId", -1)

        CoroutineScope(Dispatchers.IO).launch {
            val database = DatabaseProvider.getDatabase(context)
            val medicine = database.medicineDao().getMedicineById(medicineId)

            when (action) {
                "COMPLETE" -> {
                    cancelNotification(context, medicineId)

                    if (medicine != null) {
                        val currentCount = medicine.remainingPillCount
                        val newCount = currentCount?.minus(1)

                        // DB 업데이트
                        val updatedMedicine = medicine.copy(
                            remainingPillCount = newCount?.let { if (it < 0) 0 else it }
                        )
                        database.medicineDao().updateMedicine(updatedMedicine)

                        // 약 개수가 null(무제한)이 아니고 0이 되었을 때만 알람 취소
                        if (currentCount != null && newCount != null && newCount <= 0) {
                            AlarmHelper.cancelReminder(context, medicineId)
                        }
                    }
                }
                "SKIP" -> {
                    cancelNotification(context, medicineId)
                }
                "SNOOZE" -> {
                    cancelNotification(context, medicineId)
                    val snoozeTime = System.currentTimeMillis() + 15 * 60 * 1000
                    AlarmHelper.scheduleReminder(context, medicineId + 10000, snoozeTime, 0)
                }
            }
        }
    }

    private fun cancelNotification(context: Context, notificationId: Int) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(notificationId)
    }
}
