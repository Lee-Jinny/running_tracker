package com.jinnylee.runnningtracker.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jinnylee.runnningtracker.ui.theme.Anton
import com.jinnylee.runnningtracker.ui.theme.Background
import com.jinnylee.runnningtracker.ui.theme.Point
import com.jinnylee.runnningtracker.ui.theme.White

@Composable
fun InformationCard(
    modifier: Modifier = Modifier,
    time: String,
    distance: String
) {
    Card(
        modifier = modifier
            .fillMaxWidth(0.9f),
        colors = CardDefaults.cardColors(containerColor = Background.copy(alpha = 0.8f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "TIME", color = White.copy(alpha = 0.6f), fontSize = 12.sp, fontFamily = Anton)
                Text(text = time, color = Point, fontWeight = FontWeight.Bold, fontSize = 28.sp, fontFamily = Anton)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "DISTANCE", color = White.copy(alpha = 0.6f), fontSize = 12.sp, fontFamily = Anton)
                Text(text = distance, color = Point, fontWeight = FontWeight.Bold, fontSize = 28.sp, fontFamily = Anton)
            }
        }
    }
}
