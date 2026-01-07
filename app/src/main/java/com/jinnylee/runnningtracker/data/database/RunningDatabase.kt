package com.jinnylee.runnningtracker.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jinnylee.runnningtracker.data.dao.RunDao
import com.jinnylee.runnningtracker.data.entity.Run

@Database(entities = [Run::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class RunningDatabase : RoomDatabase() {

    abstract fun runDao(): RunDao

    // 싱글톤 패턴 (앱 전체에서 DB 인스턴스는 하나만 있어야 함)
    companion object {
        @Volatile
        private var INSTANCE: RunningDatabase? = null

        fun getDatabase(context: Context): RunningDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RunningDatabase::class.java,
                    "running_db"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}