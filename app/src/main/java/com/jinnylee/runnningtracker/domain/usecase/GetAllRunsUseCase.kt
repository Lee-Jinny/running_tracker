package com.jinnylee.runnningtracker.domain.usecase

import com.jinnylee.runnningtracker.domain.model.Run
import com.jinnylee.runnningtracker.domain.repository.RunRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllRunsUseCase @Inject constructor(
    private val runRepository: RunRepository
) {
    operator fun invoke(): Flow<List<Run>> {
        return runRepository.getAllRuns()
    }
}
