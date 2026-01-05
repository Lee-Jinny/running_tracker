package com.jinnylee.runnningtracker.presentation

import androidx.lifecycle.ViewModel
import androidx.room.util.copy
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class MainViewModel : ViewModel() {

    // UI 상태 (경로 좌표들, 시간, 거리)
    private val _runState = MutableStateFlow(RunState())
    val runState = _runState.asStateFlow()

    // ViewModel이 직접 Location 로직을 다룸 (혹은 서비스를 관찰)
    fun startRun() {
        // 서비스 시작 Intent 호출 로직
    }

    // 서비스에서 위치 데이터가 오면 UI 상태 업데이트
    fun updateLocation(newLocation: LatLng) {
        val currentList = _runState.value.pathPoints.toMutableList()
        currentList.add(newLocation)
        _runState.value = _runState.value.copy(pathPoints = currentList)
    }
}