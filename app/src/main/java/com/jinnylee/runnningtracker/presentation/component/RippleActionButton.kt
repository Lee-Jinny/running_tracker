package com.jinnylee.runnningtracker.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jinnylee.runnningtracker.ui.theme.Anton
import com.jinnylee.runnningtracker.ui.theme.Background
import com.jinnylee.runnningtracker.ui.theme.Point

@Composable
fun RippleActionButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    // 요소들을 겹치기 위해 Box 사용
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // 1. 가장 바깥쪽 레이어 (가장 연함, 가장 큼)
        Box(
            modifier = Modifier
                .size(150.dp) // 전체 크기 설정
                .clip(CircleShape)
                .background(Point.copy(alpha = 0.1f))
        )

        // 2. 중간 레이어 (중간 진하기, 중간 크기)
        Box(
            modifier = Modifier
                .size(125.dp)
                .clip(CircleShape)
                .background(Point.copy(alpha = 0.2f))
        )

        // 3. 중앙 실제 버튼 (가장 진함, 기존보다 작게 수정)
        Button(
            onClick = onClick,
            modifier = Modifier.size(100.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Point,
                contentColor = Background
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp
            )
        ) {
            Text(
                text = text,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Anton
            )
        }
    }
}

@Preview(
    name = "Dark Map Background",
    showBackground = true,
    backgroundColor = 0xFF222222 // 지도처럼 어두운 배경색 지정
)
@Composable
fun OperationButtonPreview() {
    // 버튼 주변에 여유 공간을 줘서 퍼지는 원이 잘 보이게 함
    Box(
        modifier = Modifier.padding(30.dp),
        contentAlignment = Alignment.Center
    ) {
        RippleActionButton(
            text = "GO!",
            onClick = {}
        )
    }
}

@Preview(
    name = "Stop State",
    showBackground = true,
    backgroundColor = 0xFF222222
)
@Composable
fun OperationButtonStopPreview() {
    Box(
        modifier = Modifier.padding(30.dp),
        contentAlignment = Alignment.Center
    ) {
        RippleActionButton(
            text = "STOP",
            onClick = {}
        )
    }
}