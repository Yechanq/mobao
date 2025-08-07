package com.example.mobao.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import com.example.mobao.notifications.ReminderReceiver
import java.time.LocalTime
import java.util.*

object AlarmHelper {
    private fun getAlarmManager(context: Context): AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /**
     * API 31+ 에서 정확 알람 권한이 있는지 확인하고,
     * 없으면 사용자에게 설정 화면을 띄웁니다.
     */
    private fun ensureExactAlarmAllowed(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val am = getAlarmManager(context)
            if (!am.canScheduleExactAlarms()) {
                // 설정 화면으로 이동 요청
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                throw SecurityException("Exact alarms not allowed")
            }
        }
    }

    /**
     * 여러 LocalTime 리스트를 받아 각각 정확 알람으로 등록합니다.
     */
    fun scheduleReminders(
        context: Context,
        medicineId: Int,
        times: List<LocalTime>
    ) {
        times.forEach { time ->
            scheduleDailyExactReminder(context, medicineId, time)
        }
    }

    /**
     * 모든 알람을 취소합니다.
     */
    fun cancelReminders(
        context: Context,
        medicineId: Int,
        times: List<LocalTime>
    ) {
        times.forEach { time ->
            cancelReminder(context, medicineId, time)
        }
    }

    /**
     * 하루에 한 번, 지정된 시각에 정확하게 알림을 울리도록 등록합니다.
     */
    private fun scheduleDailyExactReminder(
        context: Context,
        medicineId: Int,
        time: LocalTime
    ) {
        // 0) 정확 알람 권한 체크 (Android 12+)
        ensureExactAlarmAllowed(context)

        // 1) 다음 트리거 시점 계산
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, time.hour)
            set(Calendar.MINUTE, time.minute)
            set(Calendar.SECOND, 0)
            if (timeInMillis <= now.timeInMillis) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        // 2) 유니크한 requestCode 생성: medicineId * 10000 + hhmm
        val requestCode = medicineId * 10_000 + time.hour * 60 + time.minute
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("medicineId", medicineId)
        }
        val pi = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 3) 정확 알람 등록
        getAlarmManager(context).setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            target.timeInMillis,
            pi
        )
    }

    /**
     * 특정 시각의 알람을 취소합니다.
     */
    private fun cancelReminder(
        context: Context,
        medicineId: Int,
        time: LocalTime
    ) {
        val requestCode = medicineId * 10_000 + time.hour * 60 + time.minute
        val intent = Intent(context, ReminderReceiver::class.java)
        val pi = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        getAlarmManager(context).cancel(pi)
    }
}
