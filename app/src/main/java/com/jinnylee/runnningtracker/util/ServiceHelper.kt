package com.jinnylee.runnningtracker.util

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.jinnylee.runnningtracker.service.TrackingService

object ServiceHelper {

    // 서비스를 시작하거나 종료 명령을 전달하는 함수
    fun triggerForegroundService(context: Context, action: String) {
        Intent(context, TrackingService::class.java).apply {
            this.action = action
            // ACTION_START일 때만 startForegroundService 호출 (서비스가 없을 때 새로 띄우고 알림 등록해야 하므로)
            // 그 외(PAUSE, RESUME, STOP)는 이미 떠있는 서비스에 명령만 전달하면 되므로 startService 사용
            if (action == TrackingService.ACTION_START) {
                ContextCompat.startForegroundService(context, this)
            } else {
                context.startService(this)
            }
        }
    }
}