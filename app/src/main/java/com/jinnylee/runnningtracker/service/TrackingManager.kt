package com.jinnylee.runnningtracker.service

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object TrackingManager {
    private val _pathPoints = MutableStateFlow<List<LatLng>>(emptyList())
    val pathPoints = _pathPoints.asStateFlow()

    private val _isTracking = MutableStateFlow(false)
    val isTracking = _isTracking.asStateFlow()

    private val _durationMillis = MutableStateFlow(0L)
    val durationMillis = _durationMillis.asStateFlow()
    
    private val _distanceMeters = MutableStateFlow(0)
    val distanceMeters = _distanceMeters.asStateFlow()

    fun setTrackingState(isTracking: Boolean) {
        _isTracking.value = isTracking
    }

    fun addPathPoint(point: LatLng) {
        _pathPoints.update { it + point }
    }
    
    fun updateDuration(duration: Long) {
        _durationMillis.value = duration
    }

    fun updateDistance(distance: Int) {
        _distanceMeters.value = distance
    }
    
    fun clear() {
        _pathPoints.value = emptyList()
        _durationMillis.value = 0L
        _distanceMeters.value = 0
    }
}
