package com.jinnylee.runnningtracker.util

import java.text.SimpleDateFormat
import java.util.Date
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

    // 현재 날짜를 "Monday - March 23" 형식으로 반환 (API 24 호환)
    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("EEEE - MMMM d", Locale.ENGLISH)
        return dateFormat.format(Date())
    }

    // timestamp를 "Oct 22, 2026" 형식으로 변환
    fun formatDate(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH)
        return dateFormat.format(Date(timestamp))
    }
}