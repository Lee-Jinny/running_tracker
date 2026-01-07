package com.jinnylee.runnningtracker.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jinnylee.runnningtracker.R
import com.jinnylee.runnningtracker.ui.theme.Anton
import com.jinnylee.runnningtracker.ui.theme.White
import com.jinnylee.runnningtracker.util.TimeFormatter

@Composable
fun RunTopBar(
    modifier: Modifier = Modifier,
    onRecordClick: () -> Unit = {}
) {
    // 날짜 포맷팅 (예: Monday - March 23)
    val currentDate = remember { TimeFormatter.getCurrentDate() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 32.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // [좌측] 제목 및 날짜
        Column {
            Text(
                text = "Runnig Tracker",
                fontFamily = Anton,
                fontSize = 32.sp,
                color = White
            )
            Text(
                text = currentDate,
                fontSize = 14.sp,
                color = White.copy(alpha = 0.8f), // 살짝 투명하게
                fontWeight = FontWeight.Medium
            )
        }

        // [우측] 기록 버튼
        IconButton(
            onClick = onRecordClick,
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.Top),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_record),
                contentDescription = "Record",
                tint = White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF222222)
@Composable
fun RunTopBarPreview() {
    RunTopBar()
}