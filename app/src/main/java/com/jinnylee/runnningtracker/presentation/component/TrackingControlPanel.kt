package com.jinnylee.runnningtracker.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.maps.model.LatLng
import com.jinnylee.runnningtracker.presentation.screen.main.MainAction
import com.jinnylee.runnningtracker.presentation.screen.main.RunState
import com.jinnylee.runnningtracker.ui.theme.Background
import com.jinnylee.runnningtracker.ui.theme.Point
import com.jinnylee.runnningtracker.ui.theme.White

// [1] 컨트롤 패널 (상태에 따라 버튼 교체)
@Composable
fun TrackingControlPanel(
    modifier: Modifier = Modifier,
    state: RunState,
    onAction: (MainAction) -> Unit
) {
    // 상태 판별 로직
    val isIdle = !state.isTracking && state.pathPoints.isEmpty() // 아예 시작 전
    val isTracking = state.isTracking // 달리는 중
    val isPaused = !state.isTracking && state.pathPoints.isNotEmpty() // 일시 정지 상태

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        when {
            // 1. 처음 시작 (GO 버튼 하나, 반투명 효과 O)
            isIdle -> {
                RippleActionButton(
                    text = "GO!",
                    onClick = { onAction(MainAction.StartClicked) }
                )
            }

            // 2. 달리는 중 (STOP, SAVE 버튼 두 개, 반투명 X)
            isTracking -> {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp), // 버튼 사이 간격
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SimpleActionButton(
                        text = "STOP",
                        color = Color(0xFFFF4B4B),
                        onClick = { onAction(MainAction.PauseClicked) }
                    )
                    SimpleActionButton(
                        text = "SAVE",
                        color = Point,
                        onClick = { onAction(MainAction.SaveClicked) }
                    )
                }
            }

            // 3. 멈춘 상태 (GO, SAVE 버튼 두 개, 반투명 X)
            isPaused -> {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SimpleActionButton(
                        text = "GO",
                        color = Point,
                        onClick = { onAction(MainAction.ResumeClicked) }
                    )
                    SimpleActionButton(
                        text = "SAVE",
                        color = Background,
                        textColor = White,
                        onClick = { onAction(MainAction.SaveClicked) }
                    )
                }
            }
        }
    }
}

@Preview(name = "Idle State", showBackground = true, backgroundColor = 0xFF222222)
@Composable
fun TrackingControlPanelIdlePreview() {
    Box(modifier = Modifier.padding(24.dp)) {
        TrackingControlPanel(
            state = RunState(isTracking = false, pathPoints = emptyList()),
            onAction = {}
        )
    }
}

@Preview(name = "Tracking State", showBackground = true, backgroundColor = 0xFF222222)
@Composable
fun TrackingControlPanelTrackingPreview() {
    Box(modifier = Modifier.padding(24.dp)) {
        TrackingControlPanel(
            state = RunState(isTracking = true, pathPoints = listOf(LatLng(0.0, 0.0))),
            onAction = {}
        )
    }
}

@Preview(name = "Paused State", showBackground = true, backgroundColor = 0xFF222222)
@Composable
fun TrackingControlPanelPausedPreview() {
    Box(modifier = Modifier.padding(24.dp)) {
        TrackingControlPanel(
            state = RunState(isTracking = false, pathPoints = listOf(LatLng(0.0, 0.0))),
            onAction = {}
        )
    }
}