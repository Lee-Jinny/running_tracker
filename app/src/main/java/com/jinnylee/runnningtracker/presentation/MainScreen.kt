package com.jinnylee.runnningtracker.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.view.WindowManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.jinnylee.runnningtracker.MainActivity
import com.jinnylee.runnningtracker.component.InformationCard
import com.jinnylee.runnningtracker.component.MyLocationButton
import com.jinnylee.runnningtracker.component.OperationButton
import com.jinnylee.runnningtracker.service.TrackingService
import com.jinnylee.runnningtracker.util.ServiceHelper
import com.jinnylee.runnningtracker.util.TimeFormatter
import kotlinx.coroutines.launch


@SuppressLint("MissingPermission")
@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    val activity = context as? MainActivity
    val scope = rememberCoroutineScope()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val runState by viewModel.runState.collectAsState()

    // 1. 위치 권한 상태 관리
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // 권한 요청 런처
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    // 2. 지도 초기 위치 (서울 시청 예시, 내 위치가 잡히면 거기로 이동함)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(37.5665, 126.9780), 15f)
    }

    // 구글 맵 객체를 담을 변수
    var map: GoogleMap? by remember { mutableStateOf(null) }

    // 앱 시작 시 권한 요청 (최초 1회)
    LaunchedEffect(Unit) {
        val permissionsToRequest = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        // 안드로이드 13(Tiramisu, API 33) 이상이면 알림 권한도 추가
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        // 권한이 없는 게 하나라도 있으면 요청 팝업 띄우기
        // (간단하게 체크 로직 없이 런처 실행해도, 이미 허용된 건 알아서 패스됩니다)
        permissionLauncher.launch(permissionsToRequest.toTypedArray())
    }

    // 권한이 있거나 허용되면 자동으로 내 위치로 이동
    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
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
        }
    }
    // 운동 중 화면 꺼짐 방지 (WakeLock)
    LaunchedEffect(runState.isTracking) {
        if (runState.isTracking) {
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    // 새로운 좌표가 생길 때마다 카메라 이동 (Camera Follow)
    LaunchedEffect(runState.pathPoints) {
        if (runState.isTracking && runState.pathPoints.isNotEmpty()) {
            val lastPoint = runState.pathPoints.last()
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLng(lastPoint),
                500 // 부드럽게 이동
            )
        }
    }


    // 3. UI 레이아웃 (Box를 써서 지도 위에 UI를 겹침)
    Box(modifier = Modifier.fillMaxSize()) {

        // [Layer 1] 구글 맵
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = hasLocationPermission // 권한이 있으면 파란 점 표시
            ),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = false, // 기본 내 위치 버튼 숨김
                zoomControlsEnabled = false      // 줌 버튼 숨김
            )
        ) {
            // 지도가 로드되면 map 변수에 객체를 저장해둠 (나중에 캡처용)
            MapEffect(Unit) { googleMap ->
                map = googleMap
            }

            // pathPoints에 데이터가 들어오면 지도에 선이 그려짐
            Polyline(
                points = runState.pathPoints,
                color = androidx.compose.ui.graphics.Color.Blue,
                width = 10f
            )
        }

        // [Layer 2] 상단 정보 창 (시간, 거리)
        InformationCard(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp),
            time = TimeFormatter.getReadableTime(runState.timeDuration / 1000L),

            distance = "%.1f km".format(runState.distanceMeters / 1000f)
        )

        // [Layer 3] 하단 시작 버튼
        OperationButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),

            //상태에 따라 글자 변경 (시작 <-> 정지)
            text = if (runState.isTracking) "정지" else "시작",
            onClick = {
                // 2. 서비스 시작/종료 명령 날리기 (ServiceHelper 사용)
                if (runState.isTracking) {
                    // 이미 돌고 있으면 -> 정지 명령

                    // (A) 지도 캡처 및 저장 시도
                    map?.snapshot { bitmap ->
                        // 캡처된 이미지를 ViewModel로 보내서 저장 (null일 수도 있음)
                        if (bitmap != null) {
                            viewModel.stopRunAndSave(bitmap)
                        }
                    }
                    // (B) 종료 명령을 담아서 '시작'을 호출하면, onStartCommand가 받아서 stopSelf()함
                    ServiceHelper.triggerForegroundService(
                        context,
                        TrackingService.ACTION_STOP
                    )
                } else {
                    // 정지 상태면 -> 시작 명령
                    ServiceHelper.triggerForegroundService(
                        context,
                        TrackingService.ACTION_START
                    )
                }
            }
        )

        // [Layer 4] 내 위치로 이동 버튼 (커스텀)
        MyLocationButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 48.dp),
            onClick = {
                if (hasLocationPermission) {
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
                } else {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            }
        )
    }
}