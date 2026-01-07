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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InformationCard(
    modifier: Modifier = Modifier,
    time: String,
    distance: String
) {
    Card(
        modifier = modifier
            .fillMaxWidth(0.9f),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "시간", color = Color.Gray, fontSize = 12.sp)
                Text(text = time, fontWeight = FontWeight.Bold, fontSize = 24.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "거리", color = Color.Gray, fontSize = 12.sp)
                Text(text = distance, fontWeight = FontWeight.Bold, fontSize = 24.sp)
            }
        }
    }
}