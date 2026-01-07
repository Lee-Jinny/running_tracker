package com.jinnylee.runnningtracker.presentation.screen.main

sealed interface MainAction {
    data object StartClicked : MainAction      // 시작 버튼 클릭
    data object StopClicked : MainAction       // 정지 버튼 클릭
    data object MyLocationClicked : MainAction // 내 위치 버튼 클릭
}