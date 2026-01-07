package com.jinnylee.runnningtracker.presentation.screen.main

sealed interface MainEvent {
    data object StartTracking : MainEvent
    data object StopTracking : MainEvent
    data object PauseTracking : MainEvent
    data object ResumeTracking : MainEvent
    data object MoveToMyLocation : MainEvent
    data object SaveRun : MainEvent
}