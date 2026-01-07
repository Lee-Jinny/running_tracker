package com.jinnylee.runnningtracker.presentation.screen.main

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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
    viewModel: MainViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    
    val scope = rememberCoroutineScope()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

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
    ) { /* 필요하다면 권한 거부 시 처리 로직 추가 */ }

    // 5. [Event 처리] ViewModel에서 날아온 명령(Side Effect) 수행
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                MainEvent.StartTracking -> {
                    // 서비스 시작 명령
                    ServiceHelper.triggerForegroundService(context, TrackingService.ACTION_START)
                }
                MainEvent.StopTracking -> {
                    // (A) 지도 캡처 시도
                    googleMap?.snapshot { bitmap ->
                        if (bitmap != null) {
                            // (B) 캡처된 이미지를 ViewModel로 보내 저장
                            viewModel.saveRun(bitmap)
                        } else {
                            Toast.makeText(context, "지도 캡처 실패", Toast.LENGTH_SHORT).show()
                        }
                    }
                    // (C) 서비스 종료 명령
                    ServiceHelper.triggerForegroundService(context, TrackingService.ACTION_STOP)
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
        onMapLoaded = { map -> googleMap = map }, // 맵 객체를 Root로 전달
        onAction = viewModel::onAction            // 사용자 입력을 ViewModel로 전달
    )
}