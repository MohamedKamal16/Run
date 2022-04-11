package com.example.fitness

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

//first step to use dependency injection
//dagger is compile time so when compile must now which dependency[class variable on constructor] inject to which classes
@HiltAndroidApp
class BasicApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        //timber library to use instead of log
        Timber.plant(Timber.DebugTree())
    }
}