package com.jinnylee.runnningtracker.data.repository

import com.jinnylee.runnningtracker.data.dao.RunDao
import com.jinnylee.runnningtracker.data.mapper.toDomain
import com.jinnylee.runnningtracker.data.mapper.toEntity
import com.jinnylee.runnningtracker.domain.model.Run
import com.jinnylee.runnningtracker.domain.repository.RunRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RunRepositoryImpl @Inject constructor(
    private val runDao: RunDao
) : RunRepository {

    override suspend fun insertRun(run: Run) {
        runDao.insertRun(run.toEntity())
    }

    override suspend fun deleteRun(run: Run) {
        runDao.deleteRun(run.toEntity())
    }

    override fun getAllRuns(): Flow<List<Run>> {
        return runDao.getAllRuns().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTotalTimeInMillis(): Flow<Long> {
        return runDao.getTotalTimeInMillis()
    }

    override fun getTotalDistance(): Flow<Int> {
        return runDao.getTotalDistance()
    }

    override fun getTotalCaloriesBurned(): Flow<Int> {
        return runDao.getTotalCaloriesBurned()
    }

    override fun getTotalAvgSpeed(): Flow<Float> {
        return runDao.getTotalAvgSpeed()
    }
}