package com.jinnylee.runnningtracker.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.BatteryManager
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.jinnylee.runnningtracker.R
import com.jinnylee.runnningtracker.domain.model.Run
import com.jinnylee.runnningtracker.domain.usecase.SaveRunUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.round

@AndroidEntryPoint
class TrackingService : Service() {

    @Inject
    lateinit var saveRunUseCase: SaveRunUseCase

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var timerJob: Job? = null
    private var startTime = 0L
    private var accumulatedTime = 0L
    private var lastLocation: Location? = null
    private var isSaving = false // 중복 저장 방지 플래그

    // [1] 배터리 감시 리시버 정의
    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                val percentage = ((level / scale.toFloat()) * 100).toInt()

                // [조건] 운동 중(isTracking)이고 배터리가 20% 이하라면, 그리고 저장 중이 아니라면
                if (TrackingManager.isTracking.value && percentage <= 20 && !isSaving) {
                    stopAndSaveRun()
                }
            }
        }
    }

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

        // 배터리 리시버 등록
        registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(batteryReceiver)
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
            ACTION_PAUSE -> {
                pauseTracking()
            }
            ACTION_RESUME -> {
                if (!TrackingManager.isTracking.value) {
                    resumeTracking()
                }
            }
            ACTION_STOP -> {
                stopTracking()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf() // 서비스 종료
            }
        }
        // 시스템에 의해 강제 종료되어도 가능한 한 다시 살려내라(STICKY)
        return START_STICKY
    }

    private fun startForegroundService() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 알림 채널 만들기 (minSdk 26이므로 무조건 실행)
        val channel = NotificationChannel(
            CHANNEL_ID,
            "운동 기록 알림",
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)

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
        accumulatedTime = 0L
        lastLocation = null
        
        startLocationUpdates()
        startTimer()
    }
    
    private fun pauseTracking() {
        TrackingManager.setTrackingState(false)
        fusedLocationClient.removeLocationUpdates(locationCallback)
        timerJob?.cancel()
        accumulatedTime += System.currentTimeMillis() - startTime
    }
    
    @SuppressLint("MissingPermission")
    private fun resumeTracking() {
        TrackingManager.setTrackingState(true)
        startLocationUpdates()
        startTimer()
    }
    
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000L)
            .setMinUpdateIntervalMillis(1000L)
            .build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }
    
    private fun startTimer() {
        startTime = System.currentTimeMillis()
        timerJob = serviceScope.launch {
            while (TrackingManager.isTracking.value) {
                val timePassed = System.currentTimeMillis() - startTime + accumulatedTime
                TrackingManager.updateDuration(timePassed)
                delay(1000L)
            }
        }
    }
    
    private fun stopTracking() {
        TrackingManager.setTrackingState(false)
        TrackingManager.clear() // 데이터 초기화 (UI도 초기 상태로 복귀)
        fusedLocationClient.removeLocationUpdates(locationCallback)
        timerJob?.cancel()
        accumulatedTime = 0L
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

    // [2] 강제 저장 및 종료 로직
    private fun stopAndSaveRun() {
        // 중복 실행 방지
        if (isSaving) return
        isSaving = true

        // 서비스 생명주기와 무관하게 작업이 완료될 수 있도록 처리
        serviceScope.launch {
            try {
                // 저장 작업은 IO 스레드에서 안전하게 수행 (취소 불가)
                withContext(Dispatchers.IO + NonCancellable) {
                    // 1. 현재 데이터 가져오기
                    val distance = TrackingManager.distanceMeters.value
                    val duration = TrackingManager.durationMillis.value

                    // 2. 통계 계산
                    val distanceInKm = distance / 1000f
                    val timeInHours = duration / 1000f / 3600f
                    val avgSpeed = if (timeInHours > 0) {
                        round((distanceInKm / timeInHours) * 10) / 10f
                    } else { 0f }
                    val caloriesBurned = ((distance / 1000f) * 70).toInt()

                    // 3. Run 객체 생성 (이미지는 null)
                    val run = Run(
                        timestamp = System.currentTimeMillis(),
                        timeInMillis = duration,
                        distanceInMeters = distance,
                        avgSpeedInKMH = avgSpeed,
                        caloriesBurned = caloriesBurned,
                        img = null
                    )

                    // 4. 저장 (예외 발생 가능성 있음)
                    saveRunUseCase(run)
                }

                // 5. 알림 업데이트 (메인 스레드 가능)
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val notification = NotificationCompat.Builder(this@TrackingService, CHANNEL_ID)
                    .setContentTitle("Running Tracker")
                    .setContentText("Run saved due to low battery (< 20%)")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setAutoCancel(true)
                    .build()
                notificationManager.notify(2, notification)

                // 6. UI에 스낵바 요청 (브로드캐스트)
                val intent = Intent(ACTION_LOW_BATTERY_SAVED).apply {
                    setPackage(packageName)
                }
                sendBroadcast(intent)

            } catch (e: Exception) {
                e.printStackTrace() // 로그로 에러 확인 가능하도록 출력
            } finally {
                // 7. 종료 (무조건 실행)
                // UI 업데이트를 위해 데이터 정리
                stopTracking()
                stopForeground(STOP_FOREGROUND_REMOVE)
                delay(500L) // 시스템이 포그라운드 해제를 인지할 시간 확보
                stopSelf()
            }
        }
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_RESUME = "ACTION_RESUME"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_LOW_BATTERY_SAVED = "ACTION_LOW_BATTERY_SAVED"
        const val CHANNEL_ID = "tracking_channel"
    }
}