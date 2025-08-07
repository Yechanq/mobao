package com.example.mobao.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.mobao.data.model.DatabaseProvider
import com.example.mobao.util.AlarmHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val medicineId = intent?.getIntExtra("medicineId", -1) ?: return
        if (medicineId < 0) return

        CoroutineScope(Dispatchers.IO).launch {
            // 1) DB에서 Medicine 객체 가져오기
            val dao = DatabaseProvider.getDatabase(context).medicineDao()
            val med = dao.getMedicineByIdFlow(medicineId).firstOrNull() ?: return@launch

            val remaining = med.remainingPillCount ?: med.totalPillCount ?: 0
            if (remaining <= 0) {
                AlarmHelper.cancelReminders(context, medicineId, med.reminderTimes)
                return@launch
            }

            val name = med?.name ?: "약"
            val times = med?.reminderTimes ?: emptyList()

            // 2) 메인 스레드에서 알림 표시
            withContext(Dispatchers.Main) {
                showMedicineNotification(context, medicineId, name)
            }

            // 3) 다음 날 같은 시간 알람 재등록
            AlarmHelper.scheduleReminders(context, medicineId, times)
        }
    }

    private fun showMedicineNotification(
        context: Context,
        medicineId: Int,
        medicineName: String
    ) {
        val channelId = "medicine_reminder"
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // NotificationChannel 생성 (Android O+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (nm.getNotificationChannel(channelId) == null) {
                nm.createNotificationChannel(
                    NotificationChannel(
                        channelId,
                        "복약 알림",
                        NotificationManager.IMPORTANCE_HIGH
                    )
                )
            }
        }

        // 3가지 액션용 PendingIntents
        val completePI = PendingIntent.getBroadcast(
            context,
            medicineId,
            Intent(context, ReminderActionReceiver::class.java).apply {
                action = ReminderActionReceiver.ACTION_TAKE
                putExtra("medicineId", medicineId)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val skipPI = PendingIntent.getBroadcast(
            context,
            medicineId + 10_000,
            Intent(context, ReminderActionReceiver::class.java).apply {
                action = ReminderActionReceiver.ACTION_SKIP
                putExtra("medicineId", medicineId)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val snoozePI = PendingIntent.getBroadcast(
            context,
            medicineId + 20_000,
            Intent(context, ReminderActionReceiver::class.java).apply {
                action = ReminderActionReceiver.ACTION_SNOOZE
                putExtra("medicineId", medicineId)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 알림 빌드
        val notif = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("$medicineName 복용 알림")
            .setContentText("지금 $medicineName 복용할 시간입니다.")
            .setAutoCancel(true)
            .addAction(android.R.drawable.ic_menu_agenda, "복약 완료", completePI)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "복약 안함", skipPI)
            .addAction(android.R.drawable.ic_menu_recent_history, "다시 알림", snoozePI)
            .build()

        nm.notify(medicineId, notif)
    }
}
