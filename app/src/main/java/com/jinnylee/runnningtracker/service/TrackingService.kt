package com.jinnylee.runnningtracker.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.jinnylee.runnningtracker.R
class TrackingService : Service() {

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    // 서비스가 시작될 때 호출되는 명령 처리기
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                startForegroundService()
            }
            ACTION_STOP -> {
                stopSelf() // 서비스 종료
            }
        }
        // 시스템에 의해 강제 종료되어도 가능한 한 다시 살려내라(STICKY)
        return START_STICKY
    }

    private fun startForegroundService() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 1. 알림 채널 만들기 (Android 8.0 오레오 이상 필수)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "운동 기록 알림",
                NotificationManager.IMPORTANCE_LOW // 소리 안 나게 (계속 떠 있을 거니까)
            )
            notificationManager.createNotificationChannel(channel)
        }

        // 2. 알림 생성
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Running Trancker") // 앱 이름
            .setContentText("운동 기록 중입니다...")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // 기본 아이콘 (나중에 앱 아이콘으로 교체)
            .setOngoing(true) // 사용자가 못 지우게 설정
            .build()

        // 3. 포그라운드 서비스 시작 (이게 없으면 1분 뒤에 앱 죽음)
        startForeground(1, notification)
    }


    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val CHANNEL_ID = "tracking_channel"
    }

}