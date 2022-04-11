package com.example.fitness.ui.viewModel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitness.model.db.Run
import com.example.fitness.model.rebository.Repository
import com.example.fitness.util.SortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(val repository: Repository)
    :ViewModel() {
        fun insertRun(run: Run)=viewModelScope.launch {
            repository.insertRun(run)
        }

    private val runSortedByDate=repository.getAllFromRunSortByTimeStart()
    private val runSortedByDistance=repository.getAllFromRunSortByDistanceInMeters()
    private val runSortedByCalories=repository.getAllFromRunSortByColorizesBurnt()
    private val runSortedByTime=repository.getAllFromRunSortByTimeMillSec()
    private val runSortedByAvgSpeed=repository.getAllFromRunSortByAvgSpeed()
//Mediator live data can use more than live data logic together which observe
    val runs=MediatorLiveData<List<Run>>()
    //default sort type
    var sortType=SortType.DATE
    //The code inside the init block is the first to be executed when the class is instantiated
    init {
        runs.addSource(runSortedByDate){result->
            if (sortType==SortType.DATE){
                result.let {
                    runs.value=it }
            }
        }
         runs.addSource(runSortedByDistance){result->
            if (sortType==SortType.DISTANCE){
                result.let { runs.value=it }
            }
        }
         runs.addSource(runSortedByCalories){result->
            if (sortType==SortType.CALORIES_BURNT){
                result.let { runs.value=it }
            }
        }
         runs.addSource(runSortedByTime){result->
            if (sortType==SortType.RUNNING_TIME){
                result.let { runs.value=it }
            }
        }
         runs.addSource(runSortedByAvgSpeed){result->
            if (sortType==SortType.AVG_SPEED){
                result.let { runs.value=it }
            }
        }
    }

    fun sortRuns(sortType: SortType)=when(sortType){
        SortType.DATE->runSortedByDate.value?.let {
            runs.value=it
        }
        SortType.DISTANCE->runSortedByDistance.value?.let {runs.value=it}
        SortType.AVG_SPEED->runSortedByAvgSpeed.value?.let {runs.value=it}
        SortType.CALORIES_BURNT->runSortedByCalories.value?.let {runs.value=it}
        SortType.RUNNING_TIME->runSortedByTime.value?.let {runs.value=it}
    }.also {
        this.sortType=sortType
    }

}