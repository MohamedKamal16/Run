package com.example.fitness.model.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.fitness.model.db.Convertors
import com.example.fitness.model.db.Run
import com.example.fitness.model.db.RunDao

@Database(
    entities = [Run::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Convertors::class)
abstract class RunDataBase:RoomDatabase() {
abstract fun runDao(): RunDao
}