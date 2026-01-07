package com.jinnylee.runnningtracker.data.mapper

import com.jinnylee.runnningtracker.domain.model.Run
import com.jinnylee.runnningtracker.data.entity.Run as RunEntity

fun Run.toEntity(): RunEntity {
    return RunEntity(
        id = this.id,
        timestamp = this.timestamp,
        timeInMillis = this.timeInMillis,
        distanceInMeters = this.distanceInMeters,
        avgSpeedInKMH = this.avgSpeedInKMH,
        caloriesBurned = this.caloriesBurned,
        img = this.img
    )
}

fun RunEntity.toDomain(): Run {
    return Run(
        id = this.id,
        timestamp = this.timestamp,
        timeInMillis = this.timeInMillis,
        distanceInMeters = this.distanceInMeters,
        avgSpeedInKMH = this.avgSpeedInKMH,
        caloriesBurned = this.caloriesBurned,
        img = this.img
    )
}