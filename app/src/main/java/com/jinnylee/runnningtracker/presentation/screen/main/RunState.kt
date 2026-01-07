package com.jinnylee.runnningtracker.presentation.screen.main

import com.google.android.gms.maps.model.LatLng
data class RunState(
    val timeDuration: Long = 0L,         // 운동 시간 (초 단위 or 밀리초)
    val distanceMeters: Int = 0,         // 이동 거리 (미터)
    val calories: Int = 0,               // 소모 칼로리 (kcal)
    val isTracking: Boolean = false,     // 현재 기록 중인지 (true: 운동중, false: 정지)
    val pathPoints: List<LatLng> = emptyList(), // 지도에 그릴 경로 좌표들
)