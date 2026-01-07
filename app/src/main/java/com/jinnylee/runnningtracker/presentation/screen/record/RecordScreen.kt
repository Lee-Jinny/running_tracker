package com.jinnylee.runnningtracker.presentation.screen.record

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.jinnylee.runnningtracker.presentation.component.RecordCard
import com.jinnylee.runnningtracker.presentation.component.RecordTopBar
import com.jinnylee.runnningtracker.ui.theme.Background
import com.jinnylee.runnningtracker.ui.theme.Blue
import com.jinnylee.runnningtracker.ui.theme.Green
import com.jinnylee.runnningtracker.ui.theme.Purple
import com.jinnylee.runnningtracker.ui.theme.White
import com.jinnylee.runnningtracker.util.TimeFormatter

@Composable
fun RecordScreen(
    state: RecordState,
    modifier: Modifier = Modifier,
    onAction: (RecordAction) -> Unit
) {
    val cardColors = listOf(Blue, Green, Purple)

    Scaffold(
        topBar = {
            RecordTopBar(
                text = "Running Records",
                onBackClick = { onAction(RecordAction.OnBackClick) }
            )
        },
        containerColor = Background
    ) { paddingValues ->
        if (state.runs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No records found.",
                    color = White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(state.runs) { index, run ->
                    RecordCard(
                        date = TimeFormatter.formatDate(run.timestamp),
                        distance = String.format("%.2f", run.distanceInMeters / 1000f),
                        time = TimeFormatter.getReadableTime(run.timeInMillis / 1000L),
                        calories = "${run.caloriesBurned}",
                        backgroundColor = cardColors[index % cardColors.size]
                    )
                }
            }
        }
    }
}