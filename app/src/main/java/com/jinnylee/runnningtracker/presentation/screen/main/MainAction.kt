package com.jinnylee.runnningtracker.presentation.screen.main

sealed interface MainAction {
    data object StartClicked : MainAction      // 처음 시작 (GO)
    data object PauseClicked : MainAction      // 일시 정지 (STOP)
    data object ResumeClicked : MainAction     // 다시 시작 (GO)
    data object SaveClicked : MainAction       // 저장 및 종료 (SAVE)
    data object MyLocationClicked : MainAction // 내 위치 버튼 클릭
}