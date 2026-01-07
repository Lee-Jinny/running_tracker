package com.jinnylee.runnningtracker.domain.repository

import com.jinnylee.runnningtracker.domain.model.Run
import kotlinx.coroutines.flow.Flow

interface RunRepository {
    suspend fun insertRun(run: Run)

    suspend fun deleteRun(run: Run)

    fun getAllRuns(): Flow<List<Run>>

    fun getTotalTimeInMillis(): Flow<Long>

    fun getTotalDistance(): Flow<Int>

    fun getTotalCaloriesBurned(): Flow<Int>

    fun getTotalAvgSpeed(): Flow<Float>
}
