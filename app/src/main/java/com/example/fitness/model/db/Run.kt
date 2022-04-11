package com.example.fitness.model.db

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Run (
    var img:Bitmap?=null,
    val timeStart:Long=0L,
    val timeMillSec:Long=0L,
    val avgSpeed:Float=0f,
    val distanceInMeters:Int=0,
    val caloriesBurnt:Int=0
        ){
    @PrimaryKey(autoGenerate = true)
    var id:Int?=null
}
