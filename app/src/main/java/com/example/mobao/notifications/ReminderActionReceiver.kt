package com.example.mobao.notifications

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import com.example.mobao.data.model.DatabaseProvider
import com.example.mobao.util.AlarmHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalTime

class ReminderActionReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_TAKE   = "com.example.mobao.ACTION_TAKE"
        const val ACTION_SKIP   = "com.example.mobao.ACTION_SKIP"
        const val ACTION_SNOOZE = "com.example.mobao.ACTION_SNOOZE"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        // 1) 비동기 작업 완료 시점까지 리시버 유지
        val pendingResult = goAsync()
        val action     = intent?.action
        val medicineId = intent?.getIntExtra("medicineId", -1) ?: -1
        if (action == null || medicineId < 0) {
            pendingResult.finish()
            return
        }

        // 즉시 알림 닫기
        NotificationManagerCompat.from(context).cancel(medicineId)

        // 2) 백그라운드에서 DB 업데이트 및 알람 재스케줄링
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dao = DatabaseProvider.getDatabase(context).medicineDao()
                val med = dao.getMedicineByIdFlow(medicineId).firstOrNull()
                val times = med?.reminderTimes ?: emptyList()

                when (action) {
                    ACTION_TAKE -> {
                        med?.let {
                            val remaining = (it.remainingPillCount ?: it.totalPillCount ?: 0) - 1
                            // DB에 차감된 개수 저장
                            dao.updateMedicine(it.copy(remainingPillCount = remaining.coerceAtLeast(0)))
                            // 0 이하면 기존 알람 모두 취소
                            if (remaining <= 0) {
                                AlarmHelper.cancelReminders(context, medicineId, times)
                            }
                        }
                    }
                    ACTION_SKIP -> {
                        // 복약하지 않음: 아무것도 안 함
                    }
                    ACTION_SNOOZE -> {
                        // 15분 후 스누즈 알람
                        val snoozeTime = System.currentTimeMillis() + 15 * 60 * 1000
                        val pi = PendingIntent.getBroadcast(
                            context,
                            medicineId + 20_000,
                            Intent(context, ReminderReceiver::class.java).apply {
                                putExtra("medicineId", medicineId)
                            },
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        // 정확 알람 권한 체크
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                            !alarmMgr.canScheduleExactAlarms()
                        ) {
                            Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .also { context.startActivity(it) }
                        } else {
                            try {
                                alarmMgr.setExactAndAllowWhileIdle(
                                    AlarmManager.RTC_WAKEUP,
                                    snoozeTime,
                                    pi
                                )
                            } catch (_: SecurityException) { /* 무시 */ }
                        }
                    }
                }
            } finally {
                // 3) 리시버 종료
                pendingResult.finish()
            }
        }
    }
}
