package com.jinnylee.runnningtracker.data.entity

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "running_table")
data class Run(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,

    val timestamp: Long = 0L,     // 운동한 날짜 (밀리초)
    val timeInMillis: Long = 0L,  // 운동 시간 (밀리초)
    val distanceInMeters: Int = 0, // 이동 거리 (미터)
    val avgSpeedInKMH: Float = 0f, // 평균 속도 (km/h)
    val caloriesBurned: Int = 0,   // 소모 칼로리 (kcal)
    val img: ByteArray?     // 지도 스크린샷 이미지
)