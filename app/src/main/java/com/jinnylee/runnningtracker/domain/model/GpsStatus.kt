package com.jinnylee.runnningtracker.domain.model

sealed interface GpsStatus {
    // GPS가 꺼져 있는 상태
    data object Disabled : GpsStatus
    // GPS 신호 찾는 중
    data object Searching : GpsStatus
    // GPS 수신 중
    data object Fixed : GpsStatus
    // GPS 신호 이탈
    data object Lost: GpsStatus
}