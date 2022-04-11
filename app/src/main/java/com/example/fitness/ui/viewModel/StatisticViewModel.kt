package com.example.fitness.ui.viewModel

import androidx.lifecycle.ViewModel
import com.example.fitness.model.rebository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticViewModel @Inject constructor( val repository: Repository) : ViewModel() {
    val totalTimeRun = repository.getTotalTimeMillSec()
    val totalDistance = repository.getTotalDistanceInMeters()
    val totalAvgSpeed = repository.getTotalAvgSpeed()
    val totalCaloriesBurned = repository.getTotalColorizesBurnt()

     val runSortedByDate = repository.getAllFromRunSortByTimeStart()


}