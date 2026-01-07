package com.jinnylee.runnningtracker.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jinnylee.runnningtracker.R
import com.jinnylee.runnningtracker.ui.theme.Anton
import com.jinnylee.runnningtracker.ui.theme.Blue
import com.jinnylee.runnningtracker.ui.theme.Green
import com.jinnylee.runnningtracker.ui.theme.Purple

@Composable
fun RecordCard(
    modifier: Modifier = Modifier,
    date: String,        // 예: "Oct 22, 2026" (월, 일, 년)
    distance: String,    // 예: "6.27"
    time: String,        // 예: "00:45:23"
    calories: String,    // 예: "568"
    backgroundColor: Color = Color(0xFF4B98F5) // 기본 파란색
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(24.dp), // 둥근 모서리
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // [상단] 아이콘 + 날짜 (월, 일, 년)
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 아이콘 배경 (반투명 원)
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)), // 20% 투명한 흰색
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_sprint),
                        contentDescription = "Run Icon",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // 날짜 텍스트
                Text(
                    text = date,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // [중단] 거리 표시 (큰 폰트)
            Row(
                verticalAlignment = Alignment.Bottom // 텍스트 라인 맞춤
            ) {
                Text(
                    text = distance,
                    fontFamily = Anton,
                    fontSize = 50.sp,   // 아주 크게
                    color = Color.White,
                    fontStyle = FontStyle.Italic // 속도감을 위해 이탤릭(선택사항)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "km",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 10.dp) // 숫자와 베이스라인 맞추기
                )
            }

            // [하단] 시간 + 칼로리 (양 끝 배치)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 걸린 시간
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = time,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.width(7.dp))
                    Text(
                        text = "Time",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )

                }

                // 칼로리
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "$calories",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.width(7.dp))
                    Text(
                        text = "kcal",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )

                }
            }
        }
    }
}

// 🎨 프리뷰: 다양한 색상 테스트
@Preview(showBackground = true)
@Composable
fun RecordCardPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. 파란색 카드
        RecordCard(
            date = "Oct 22, 2026",
            distance = "6.27",
            time = "00:42:15",
            calories = "568",
            backgroundColor = Blue
        )

        // 2. 초록색 카드
        RecordCard(
            date = "Oct 20, 2026",
            distance = "10.54",
            time = "01:15:30",
            calories = "1,257",
            backgroundColor = Green
        )

        // 3. 보라색 카드
        RecordCard(
            date = "Oct 18, 2026",
            distance = "4.92",
            time = "00:30:10",
            calories = "420",
            backgroundColor = Purple
        )
    }
}