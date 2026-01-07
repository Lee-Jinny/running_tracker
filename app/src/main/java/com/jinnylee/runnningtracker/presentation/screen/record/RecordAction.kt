package com.jinnylee.runnningtracker.presentation.screen.record

sealed interface RecordAction {
    data object OnBackClick : RecordAction
}