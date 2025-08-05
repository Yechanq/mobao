package com.example.mobao.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.mobao.R


class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val medicineId = intent.getIntExtra("medicineId", -1)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "medicine_reminder"

        // Android 8 이상 채널 등록
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "복약 알림",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val completeIntent = Intent(context, ReminderActionReceiver::class.java).apply {
            action = "COMPLETE"
            putExtra("medicineId", medicineId)
        }
        val skipIntent = Intent(context, ReminderActionReceiver::class.java).apply {
            action = "SKIP"
            putExtra("medicineId", medicineId)
        }
        val snoozeIntent = Intent(context, ReminderActionReceiver::class.java).apply {
            action = "SNOOZE"
            putExtra("medicineId", medicineId)
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // 기본 아이콘 사용
            .setContentTitle("복약 시간입니다")
            .setContentText("지금 약을 복용하세요.")
            .setAutoCancel(true)
            .addAction(0, "복약 완료", PendingIntent.getBroadcast(
                context, medicineId + 100,
                completeIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            ))
            .addAction(0, "건너뛰기", PendingIntent.getBroadcast(
                context, medicineId + 200,
                skipIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            ))
            .addAction(0, "다시 알림", PendingIntent.getBroadcast(
                context, medicineId + 300,
                snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            ))

        notificationManager.notify(medicineId, builder.build())
    }
}
