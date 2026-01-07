package com.jinnylee.runnningtracker.domain.usecase

import com.jinnylee.runnningtracker.domain.repository.RunRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetTotalStatusUseCase @Inject constructor(
    private val runRepository: RunRepository
) {
    operator fun invoke(): Flow<TotalStatus> {
        return combine(
            runRepository.getTotalTimeInMillis(),
            runRepository.getTotalDistance(),
            runRepository.getTotalCaloriesBurned(),
            runRepository.getTotalAvgSpeed()
        ) { time, distance, calories, avgSpeed ->
            TotalStatus(
                totalTimeInMillis = time ?: 0L,
                totalDistance = distance ?: 0,
                totalCaloriesBurned = calories ?: 0,
                totalAvgSpeed = avgSpeed ?: 0f
            )
        }
    }

    data class TotalStatus(
        val totalTimeInMillis: Long,
        val totalDistance: Int,
        val totalCaloriesBurned: Int,
        val totalAvgSpeed: Float
    )
}
