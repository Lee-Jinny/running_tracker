package com.jinnylee.runnningtracker.presentation.screen.main

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import com.jinnylee.runnningtracker.service.TrackingService
import com.jinnylee.runnningtracker.util.ServiceHelper
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun MainRoot(
    viewModel: MainViewModel = hiltViewModel(),
    onNavigateToRecord: () -> Unit = {}
) {
    val context = LocalContext.current
    
    val scope = rememberCoroutineScope()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    
    // 스낵바 상태 관리
    val snackbarHostState = remember { SnackbarHostState() }

    // 배터리 부족 저장 감지 리시버
    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {//
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == TrackingService.ACTION_LOW_BATTERY_SAVED) {
                    scope.launch {
                        snackbarHostState.showSnackbar("Run saved due to low battery.")
                    }
                }
            }
        }
        val filter = IntentFilter(TrackingService.ACTION_LOW_BATTERY_SAVED)
        
        // ContextCompat을 사용하여 최신 보안 정책(Exported/NotExported) 준수 및 하위 호환성 확보
        ContextCompat.registerReceiver(
            context,
            receiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    // 1. ViewModel의 State 관찰 (UI 업데이트용)
    val state by viewModel.state.collectAsState()

    // 2. 지도 객체 참조 (스냅샷 캡처용)
    var googleMap: GoogleMap? by remember { mutableStateOf(null) }

    // 3. 카메라 상태 관리
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(37.5665, 126.9780), 15f)
    }

    // 4. 권한 요청 런처
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // 권한 허용 시 초기 위치로 이동
        val isGranted = permissions.values.all { it }
        if (isGranted) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        scope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(it.latitude, it.longitude),
                                    17f
                                )
                            )
                        }
                    }
                }
            } catch (e: SecurityException) {
                // Ignore
            }
        }
    }

    // 5. [Event 처리] ViewModel에서 날아온 명령(Side Effect) 수행
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                MainEvent.StartTracking -> {
                    // 서비스 시작 명령
                    ServiceHelper.triggerForegroundService(context, TrackingService.ACTION_START)
                }
                MainEvent.PauseTracking -> {
                    // 서비스 일시 정지 명령
                    ServiceHelper.triggerForegroundService(context, TrackingService.ACTION_PAUSE)
                }
                MainEvent.ResumeTracking -> {
                    // 서비스 재개 명령
                    ServiceHelper.triggerForegroundService(context, TrackingService.ACTION_RESUME)
                }
                MainEvent.SaveRun -> {
                    // (A) 현재 상태 캡처 (스냅샷 찍는 동안 상태가 초기화될 수 있으므로 미리 변수에 저장)
                    val currentPoints = state.pathPoints
                    val currentDuration = state.timeDuration
                    val currentDistance = state.distanceMeters

                    // (B) 지도 캡처 시도
                    googleMap?.snapshot { bitmap ->
                        if (bitmap != null) {
                            // (C) 캡처된 이미지와 미리 저장해둔 데이터를 ViewModel로 보내 저장
                            viewModel.saveRun(bitmap, currentPoints, currentDuration, currentDistance)
                        } else {
                            Toast.makeText(context, "지도 캡처 실패", Toast.LENGTH_SHORT).show()
                        }
                        // (D) 저장 로직 호출 후 서비스 종료 명령 (순서 중요: 데이터 확보 후 종료)
                        ServiceHelper.triggerForegroundService(context, TrackingService.ACTION_STOP)
                    }
                }
                MainEvent.StopTracking -> {
                    // (Deprecated or fallback) 서비스 종료
                    ServiceHelper.triggerForegroundService(context, TrackingService.ACTION_STOP)
                }
                MainEvent.NavigateToRecord -> {
                    onNavigateToRecord()
                }
                is MainEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                MainEvent.ShowBatteryWarning -> {
                    snackbarHostState.showSnackbar("Battery is low (under 30%).")
                }
                MainEvent.MoveToMyLocation -> {
                    // 내 위치로 카메라 이동
                    try {
                        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                            location?.let {
                                scope.launch {
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLngZoom(
                                            LatLng(it.latitude, it.longitude),
                                            17f
                                        )
                                    )
                                }
                            }
                        }
                    } catch (e: SecurityException) {
                        // 권한이 없으면 다시 요청
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                }
            }
        }
    }

    // 6. 초기 권한 요청 (앱 진입 시)
    LaunchedEffect(Unit) {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        permissionLauncher.launch(permissions.toTypedArray())
    }

    // 7. 카메라 경로 추적 (Tracking 중일 때만)
    LaunchedEffect(state.pathPoints) {
        if (state.isTracking && state.pathPoints.isNotEmpty()) {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLng(state.pathPoints.last()),
                500
            )
        }
    }

    // 8. UI 표시 (MainScreen 호출)
    MainScreen(
        state = state,
        cameraPositionState = cameraPositionState,
        snackbarHostState = snackbarHostState,
        onMapLoaded = { map -> googleMap = map }, // 맵 객체를 Root로 전달
        onAction = viewModel::onAction            // 사용자 입력을 ViewModel로 전달
    )
}