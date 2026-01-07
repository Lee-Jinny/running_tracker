package com.jinnylee.runnningtracker.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jinnylee.runnningtracker.data.entity.Run
import kotlinx.coroutines.flow.Flow

@Dao
interface RunDao {

    // 운동 기록 저장 (이미 있으면 덮어쓰기 말고 교체)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(run: Run)

    // 운동 기록 삭제
    @androidx.room.Delete
    suspend fun deleteRun(run: Run)

    // 저장된 모든 기록 가져오기 (날짜 최신순 정렬)
    @Query("SELECT * FROM running_table ORDER BY timestamp DESC")
    fun getAllRuns(): Flow<List<Run>>

    // 총 운동 시간 가져오기 (통계용)
    @Query("SELECT SUM(timeInMillis) FROM running_table")
    fun getTotalTimeInMillis(): Flow<Long>

    // 총 거리 가져오기 (통계용)
    @Query("SELECT SUM(distanceInMeters) FROM running_table")
    fun getTotalDistance(): Flow<Int>

    // 총 소모 칼로리 가져오기 (통계용)
    @Query("SELECT SUM(caloriesBurned) FROM running_table")
    fun getTotalCaloriesBurned(): Flow<Int>

    // 평균 속도 가져오기 (통계용 - 전체 평균)
    @Query("SELECT AVG(avgSpeedInKMH) FROM running_table")
    fun getTotalAvgSpeed(): Flow<Float>
}