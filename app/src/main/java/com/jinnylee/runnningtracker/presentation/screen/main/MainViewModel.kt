package com.jinnylee.runnningtracker.presentation.screen.main

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jinnylee.runnningtracker.data.database.RunningDatabase
import com.jinnylee.runnningtracker.data.entity.Run
import com.jinnylee.runnningtracker.service.TrackingManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.round

class MainViewModel(
    application: Application
) : AndroidViewModel(application) {

    // 1. DB 인스턴스 생성
    private val runDao = RunningDatabase.getDatabase(application).runDao()


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
    fun toggleTracking() {/* ServiceHelper가 처리하므로 비워둠 */ }
    // [추가] 운동 종료 시 호출: 스냅샷을 받아서 저장
    fun stopRunAndSave(bitmap: Bitmap?) {
        viewModelScope.launch {
            val currentState = runState.value

            // 데이터가 없거나 너무 짧으면 저장 안 함 (예외 처리)
            if(currentState.pathPoints.isEmpty()) return@launch

            // 1. 평균 속도 계산 (km/h)
            // 거리(m) / 1000 = km
            // 시간(ms) / 1000 / 3600 = hour
            val distanceInKm = currentState.distanceMeters / 1000f
            val timeInHours = currentState.timeDuration / 1000f / 3600f

            val avgSpeed = if (timeInHours > 0) {
                round((distanceInKm / timeInHours) * 10) / 10f // 소수점 한자리 반올림
            } else {
                0f
            }

            // 2. 칼로리 계산 (단순 공식: 거리(km) * 몸무게(70kg 가정))
            // 좀 더 정확한 공식: METs 등을 써야 하지만 일단 간단하게
            val caloriesBurned = ((currentState.distanceMeters / 1000f) * 70).toInt()

            // 3. Run 객체 생성
            val run = Run(
                timestamp = System.currentTimeMillis(),
                timeInMillis = currentState.timeDuration,
                distanceInMeters = currentState.distanceMeters,
                avgSpeedInKMH = avgSpeed,
                caloriesBurned = caloriesBurned,
                img = bitmap // 지도 스크린샷
            )

            // 4. DB 저장
            runDao.insertRun(run)
        }
    }
}