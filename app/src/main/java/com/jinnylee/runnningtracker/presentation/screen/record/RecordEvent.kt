package com.jinnylee.runnningtracker.presentation.screen.record

sealed interface RecordEvent {
    data object NavigateBack : RecordEvent
}