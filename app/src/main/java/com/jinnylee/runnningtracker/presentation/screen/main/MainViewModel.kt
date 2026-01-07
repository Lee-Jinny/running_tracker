package com.jinnylee.runnningtracker.presentation.screen.main

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jinnylee.runnningtracker.domain.model.Run
import com.jinnylee.runnningtracker.domain.usecase.SaveRunUseCase
import com.jinnylee.runnningtracker.presentation.screen.main.MainAction
import com.jinnylee.runnningtracker.presentation.screen.main.MainEvent
import com.jinnylee.runnningtracker.presentation.screen.main.RunState
import com.jinnylee.runnningtracker.service.TrackingManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.round

@HiltViewModel
class MainViewModel @Inject constructor(
    private val saveRunUseCase: SaveRunUseCase
) : ViewModel() {

    // TrackingManager의 데이터를 관찰하여 UI 상태(RunState)로 변환
    // combine의 결과 자체가 Flow이므로 stateIn을 써서 StateFlow로 만듦
    val state: StateFlow<RunState> = combine(
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

    private val _event = MutableSharedFlow<MainEvent>()
    val event = _event.asSharedFlow()

    fun onAction(action: MainAction) {
        viewModelScope.launch {
            when(action) {
                MainAction.StopClicked -> {
                    _event.emit(MainEvent.StopTracking)
                }
                MainAction.StartClicked -> {
                    _event.emit(MainEvent.StartTracking)
                }
                MainAction.MyLocationClicked -> {
                    _event.emit(MainEvent.MoveToMyLocation)
                }
            }
        }
    }

    // UI에서 호출하지만 실제 상태 변경은 Service -> TrackingManager -> runState 흐름을 따름
    fun toggleTracking() {/* ServiceHelper가 처리하므로 비워둠 */ }
    // [추가] 운동 종료 시 호출: 스냅샷을 받아서 저장
    fun saveRun(bitmap: Bitmap?) {
        viewModelScope.launch {
            val currentState = state.value

            // 데이터가 없거나 경로가 비었으면 저장하지 않음
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
            saveRunUseCase(run)
        }
    }
}