package com.jinnylee.runnningtracker.presentation.screen.record

import com.jinnylee.runnningtracker.domain.model.Run

data class RecordState(
    val runs: List<Run> = emptyList()
)