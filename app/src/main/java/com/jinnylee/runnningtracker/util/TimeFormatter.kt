package com.jinnylee.runnningtracker.util

import java.util.Locale

object TimeFormatter {

    // 초(Seconds)를 받아서 "00:00" 또는 "01:00:00" 형식으로 변환
    fun getReadableTime(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val remainingSeconds = seconds % 60

        return if (hours > 0) {
            // 1시간 이상일 때: 01:30:45
            String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, remainingSeconds)
        } else {
            // 1시간 미만일 때: 30:45
            String.format(Locale.getDefault(), "%02d:%02d", minutes, remainingSeconds)
        }
    }
}