// notifications/ReminderActionReceiver.kt
package com.example.mobao.notifications

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.mobao.data.model.DatabaseProvider
import com.example.mobao.util.AlarmHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ReminderActionReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_TAKE   = "com.example.mobao.ACTION_TAKE"
        const val ACTION_SKIP   = "com.example.mobao.ACTION_SKIP"
        const val ACTION_SNOOZE = "com.example.mobao.ACTION_SNOOZE"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        val action     = intent?.action ?: return
        val medicineId = intent.getIntExtra("medicineId", -1)
        if (medicineId < 0) return

        // 즉시 알림 닫기
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(medicineId)

        CoroutineScope(Dispatchers.IO).launch {
            // DB에서 약 정보 가져오기
            val dao = DatabaseProvider.getDatabase(context).medicineDao()
            val med = dao.getMedicineByIdFlow(medicineId).firstOrNull()
            val times = med?.reminderTimes ?: emptyList()

            when (action) {
                ACTION_TAKE -> {
                    // 복용 완료: 남은 개수 -1, 0이 되면 알람 전부 취소
                    med?.let {
                        val remaining = (it.remainingPillCount ?: it.totalPillCount ?: 0) - 1
                        dao.updateMedicine(it.copy(remainingPillCount = remaining.coerceAtLeast(0)))
                        if (remaining <= 0) {
                            AlarmHelper.cancelReminders(context, medicineId, times)
                        }
                    }
                }
                ACTION_SKIP -> {
                    // 건너뛰기: 아무 동작 없이 알림만 취소
                }
                ACTION_SNOOZE -> {
                    // 스누즈: 15분 뒤에 다시 알림
                    val snoozeTime = System.currentTimeMillis() + 15 * 60 * 1000
                    val pi = PendingIntent.getBroadcast(
                        context,
                        medicineId + 20_000,  // unique requestCode
                        Intent(context, ReminderReceiver::class.java).apply {
                            putExtra("medicineId", medicineId)
                        },
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    alarmMgr.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        snoozeTime,
                        pi
                    )
                }
            }
        }
    }
}
