package com.jinnylee.runnningtracker.presentation.screen.main

sealed interface MainEvent {
    data object StartTracking : MainEvent
    data object StopTracking : MainEvent
    data object MoveToMyLocation : MainEvent
}