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
            // 명령을 전달하는 것이므로 시작할 때 버전 체크를 자동으로 해주는 ContextCompat 사용
            // 종료 명령(ACTION_STOP)을 담아서 호출해도 onStartCommand가 받아서 처리함
            ContextCompat.startForegroundService(context, this)
        }
    }
}