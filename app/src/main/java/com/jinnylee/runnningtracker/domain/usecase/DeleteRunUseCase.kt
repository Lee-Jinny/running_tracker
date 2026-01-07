package com.jinnylee.runnningtracker.domain.usecase

import com.jinnylee.runnningtracker.domain.model.Run
import com.jinnylee.runnningtracker.domain.repository.RunRepository
import javax.inject.Inject

class DeleteRunUseCase @Inject constructor(
    private val runRepository: RunRepository
) {
    suspend operator fun invoke(run: Run) {
        runRepository.deleteRun(run)
    }
}