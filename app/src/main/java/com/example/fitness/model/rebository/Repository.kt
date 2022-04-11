package com.example.fitness.model.rebository

import com.example.fitness.model.db.Run
import com.example.fitness.model.db.RunDao
import javax.inject.Inject

class Repository @Inject constructor(
    private val runDao: RunDao
) {
    // suspend because we run it in coroutine because it run synchronously
    /**
 1.   Synchronous, or Synchronized means “connected”, or “dependent” in some way.
    When you execute something synchronously,** you wait for it to finish before moving on to another task.**
    In other words, Synchronous execution means the execution happens in a [single series].

     **so we use suspend fun to use coroutine
       and we don't use it in get method because its return livedata that work synchronously**
     */
    suspend fun insertRun(run: Run)=runDao.insertRun(run)

    suspend fun deleteRun(run: Run)=runDao.deleteRun(run)

    fun getAllFromRunSortByTimeStart()=runDao.getAllFromRunSortByTimeStart()

    fun getAllFromRunSortByTimeMillSec()=runDao.getAllFromRunSortByTimeMillSec()

    fun getAllFromRunSortByAvgSpeed()=runDao.getAllFromRunSortByAvgSpeed()

    fun getAllFromRunSortByDistanceInMeters() =runDao.getAllFromRunSortByDistanceInMeters()

    fun getAllFromRunSortByColorizesBurnt() =runDao.getAllFromRunSortByColorizesBurnt()

    fun getTotalTimeMillSec() =runDao.getTotalTimeMillSec()

    fun getTotalAvgSpeed() =runDao.getTotalAvgSpeed()

    fun getTotalDistanceInMeters() =runDao.getTotalDistanceInMeters()

    fun getTotalColorizesBurnt() =runDao.getTotalColorizesBurnt()
}