package com.jinnylee.runnningtracker.util

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import kotlin.math.roundToInt

object BatteryUtil {
    fun getBatteryPercentage(context: Context) : Int {
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)

        // null 리시버를 등록하면 현재 상태의 Sticky Intent(마지막 배터리 정보)만 반환
        val batteryStatus: Intent? = context.registerReceiver(null, intentFilter)

        val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1

        return if (level != -1 && scale != -1) {
            ((level / scale.toFloat()) * 100).roundToInt()
        } else {
            100 // 확인 불가 시 100으로 가정 (에러 방지)
        }

    }
}