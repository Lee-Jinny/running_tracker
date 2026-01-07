package com.jinnylee.runnningtracker.core.di

import android.content.Context
import com.jinnylee.runnningtracker.data.dao.RunDao
import com.jinnylee.runnningtracker.data.database.RunningDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideRunningDatabase(
        @ApplicationContext context: Context
    ): RunningDatabase {
        return RunningDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideRunDao(database: RunningDatabase): RunDao {
        return database.runDao()
    }
}
