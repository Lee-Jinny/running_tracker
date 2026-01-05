package com.jinnylee.runnningtracker.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.jinnylee.runnningtracker.service.TrackingManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class MainViewModel : ViewModel() {

    // TrackingManager의 데이터를 관찰하여 UI 상태(RunState)로 변환
    val runState = combine(
        TrackingManager.pathPoints,
        TrackingManager.durationMillis,
        TrackingManager.distanceMeters,
        TrackingManager.isTracking
    ) { points, time, distance, isTracking ->
        RunState(
            timeDuration = time,
            distanceMeters = distance,
            isTracking = isTracking,
            pathPoints = points
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RunState()
    )

    // UI에서 호출하지만 실제 상태 변경은 Service -> TrackingManager -> runState 흐름을 따름
    fun toggleTracking() {
        // 여기서는 아무것도 안 해도 됨. ServiceHelper가 서비스를 시작/종료하면
        // Service가 TrackingManager를 업데이트하고, 그게 runState에 반영됨.
    }
}