package com.jinnylee.runnningtracker.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.jinnylee.runnningtracker.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TrackingService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var timerJob: Job? = null
    private var startTime = 0L
    private var lastLocation: Location? = null

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                result.locations.forEach { location ->
                    addPathPoint(location)
                }
            }
        }
    }

    // 서비스가 시작될 때 호출되는 명령 처리기
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                if (!TrackingManager.isTracking.value) {
                    TrackingManager.clear()
                    startForegroundService()
                    startTracking()
                }
            }
            ACTION_STOP -> {
                stopTracking()
                stopSelf() // 서비스 종료
            }
        }
        // 시스템에 의해 강제 종료되어도 가능한 한 다시 살려내라(STICKY)
        return START_STICKY
    }

    private fun startForegroundService() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 1. 알림 채널 만들기 (Android 8.0 오레오 이상 필수)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "운동 기록 알림",
                NotificationManager.IMPORTANCE_LOW // 소리 안 나게 (계속 떠 있을 거니까)
            )
            notificationManager.createNotificationChannel(channel)
        }

        // 2. 알림 생성
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Running Tracker") // 앱 이름
            .setContentText("운동 기록 중입니다...")
            .setSmallIcon(R.drawable.ic_launcher_foreground) 
            .setOngoing(true) // 사용자가 못 지우게 설정
            .build()

        // 3. 포그라운드 서비스 시작
        startForeground(1, notification)
    }

    @SuppressLint("MissingPermission")
    private fun startTracking() {
        TrackingManager.setTrackingState(true)

        // 지난번 운동의 마지막 위치 기록을 지워주기
        lastLocation = null
        
        // 위치 요청 설정
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000L)
            .setMinUpdateIntervalMillis(1000L)
            .build()

        // 위치 업데이트 시작
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
        
        // 타이머 시작
        startTime = System.currentTimeMillis()
        timerJob = serviceScope.launch {
            while (TrackingManager.isTracking.value) {
                val elapsed = System.currentTimeMillis() - startTime
                delay(1000L) // 1초마다 갱신하되

                // 현재 시간 - 시작 시간 = 정확한 경과 시간
                val timePassed = System.currentTimeMillis() - startTime
                TrackingManager.updateDuration(timePassed)
            }
        }
    }
    
    private fun stopTracking() {
        TrackingManager.setTrackingState(false)
        fusedLocationClient.removeLocationUpdates(locationCallback)
        timerJob?.cancel()
    }
    
    private fun addPathPoint(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        TrackingManager.addPathPoint(latLng)
        
        // 거리 계산
        if (lastLocation != null) {
            val distance = lastLocation?.distanceTo(location) ?: 0f
            val currentDistance = TrackingManager.distanceMeters.value
            TrackingManager.updateDistance(currentDistance + distance.toInt())
        }
        lastLocation = location
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val CHANNEL_ID = "tracking_channel"
    }

}
