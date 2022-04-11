package com.example.fitness.model.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.fitness.model.db.Run

@Dao
interface RunDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(run: Run)

    @Delete
    suspend fun deleteRun(run: Run)

    @Query("Select * From Run ORDER BY  timeStart DESC")
    fun getAllFromRunSortByTimeStart():LiveData<List<Run>>

    @Query("Select * From Run ORDER BY  timeMillSec DESC")
    fun getAllFromRunSortByTimeMillSec():LiveData<List<Run>>

    @Query("Select * From Run ORDER BY  avgSpeed DESC")
    fun getAllFromRunSortByAvgSpeed():LiveData<List<Run>>

    @Query("Select * From Run ORDER BY  distanceInMeters DESC")
    fun getAllFromRunSortByDistanceInMeters():LiveData<List<Run>>

    @Query("Select * From Run ORDER BY  caloriesBurnt DESC")
    fun getAllFromRunSortByColorizesBurnt():LiveData<List<Run>>

    @Query("Select SUM(timeMillSec) From Run ")
    fun getTotalTimeMillSec():LiveData<Long>

    @Query("Select AVG(avgSpeed) From Run ")
    fun getTotalAvgSpeed():LiveData<Float>

    @Query("Select SUM(distanceInMeters) From Run ")
    fun getTotalDistanceInMeters():LiveData<Int>

    @Query("Select SUM(caloriesBurnt) From Run ")
    fun getTotalColorizesBurnt():LiveData<Int>

}
