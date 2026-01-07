package com.jinnylee.runnningtracker.core.di

import com.jinnylee.runnningtracker.data.repository.RunRepositoryImpl
import com.jinnylee.runnningtracker.domain.repository.RunRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRunRepository(
        runRepositoryImpl: RunRepositoryImpl
    ): RunRepository
}