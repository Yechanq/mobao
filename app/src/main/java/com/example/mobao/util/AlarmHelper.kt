package com.example.mobao.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.mobao.notifications.ReminderReceiver


object AlarmHelper {

    fun scheduleReminder(
        context: Context,
        medicineId: Int,
        timeInMillis: Long,
        repeatIntervalMillis: Long // 하루 3회면 8시간 간격
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("medicineId", medicineId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            medicineId, // 고유 ID로 구분
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            repeatIntervalMillis,
            pendingIntent
        )
    }

    fun cancelReminder(context: Context, medicineId: Int) {
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            medicineId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }
}
