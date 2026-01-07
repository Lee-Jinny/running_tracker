package com.jinnylee.runnningtracker.presentation.screen.main

import android.annotation.SuppressLint
import android.view.WindowManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.jinnylee.runnningtracker.MainActivity
import com.jinnylee.runnningtracker.presentation.component.InformationCard
import com.jinnylee.runnningtracker.presentation.component.MyLocationButton
import com.jinnylee.runnningtracker.presentation.component.OperationButton
import com.jinnylee.runnningtracker.util.TimeFormatter

@SuppressLint("MissingPermission")
@Composable
fun MainScreen(
    state: RunState,                  // 보여줄 데이터
    cameraPositionState: CameraPositionState, // 지도 카메라 상태
    onMapLoaded: (GoogleMap) -> Unit, // 지도 객체 전달 콜백
    onAction: (MainAction) -> Unit    // 사용자 클릭 전달 콜백
) {
    val context = LocalContext.current
    val activity = context as? MainActivity

    // 운동 중 화면 꺼짐 방지
    LaunchedEffect(state.isTracking) {
        if (state.isTracking) {
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // [Layer 1] 구글 맵
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = true),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = false,
                zoomControlsEnabled = false
            )
        ) {
            // 지도가 준비되면 Root에게 알림 (캡처 준비)
            MapEffect(Unit) { map ->
                onMapLoaded(map)
            }

            // 이동 경로 그리기
            Polyline(
                points = state.pathPoints,
                color = Color.Blue,
                width = 10f
            )
        }

        // [Layer 2] 상단 정보 창 (시간, 거리)
        InformationCard(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp),
            // TimeFormatter에는 초(sec) 단위를 넘겨야 하므로 / 1000
            time = TimeFormatter.getReadableTime(state.timeDuration / 1000L),
            distance = "%.1f km".format(state.distanceMeters / 1000f)
        )

        // [Layer 3] 하단 시작/정지 버튼
        OperationButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            text = if (state.isTracking) "정지" else "시작",
            onClick = {
                // 사용자의 의도(Action)를 ViewModel로 전달
                if (state.isTracking) {
                    onAction(MainAction.StopClicked)
                } else {
                    onAction(MainAction.StartClicked)
                }
            }
        )

        // [Layer 4] 내 위치 버튼
        MyLocationButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 48.dp),
            onClick = {
                onAction(MainAction.MyLocationClicked)
            }
        )
    }
}


// 1. 운동 중일 때의 미리보기 (데이터가 있는 상태)
@Preview(showBackground = true, name = "Tracking State")
@Composable
fun MainScreenTrackingPreview() {
    // 가짜 데이터 생성
    val samplePath = listOf(
        LatLng(37.5665, 126.9780),
        LatLng(37.5666, 126.9781),
        LatLng(37.5667, 126.9782)
    )

    // 가짜 상태 (1시간 운동, 5km 이동)
    val mockState = RunState(
        isTracking = true,
        timeDuration = 3600000L, // 1시간 (밀리초)
        distanceMeters = 5000,   // 5000미터
        pathPoints = samplePath
    )

    // 카메라 위치 (서울 시청)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(37.5665, 126.9780), 15f)
    }

    MainScreen(
        state = mockState,
        cameraPositionState = cameraPositionState,
        onMapLoaded = {}, // 프리뷰에선 동작 안 하므로 빈 람다
        onAction = {}     // 프리뷰에선 동작 안 하므로 빈 람다
    )
}

// 2. 운동 전 대기 상태 미리보기
@Preview(showBackground = true, name = "Idle State")
@Composable
fun MainScreenIdlePreview() {
    val mockState = RunState(
        isTracking = false,
        timeDuration = 0L,
        distanceMeters = 0,
        pathPoints = emptyList()
    )

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(37.5665, 126.9780), 15f)
    }

    MainScreen(
        state = mockState,
        cameraPositionState = cameraPositionState,
        onMapLoaded = {},
        onAction = {}
    )
}