package com.example.fitness.ui.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.example.fitness.R
import com.example.fitness.util.Constant.ACTION_PAUSE_SERVICE
import com.example.fitness.util.Constant.ACTION_START_SERVICE
import com.example.fitness.util.Constant.ACTION_STOP_SERVICE
import com.example.fitness.util.Constant.FASTET_Location_Interval
import com.example.fitness.util.Constant.Location_update_Interval
import com.example.fitness.util.Constant.NOTIFICATION_CHANNEL_ID
import com.example.fitness.util.Constant.NOTIFICATION_CHANNEL_NAME
import com.example.fitness.util.Constant.NOTIFICATION_ID
import com.example.fitness.util.TrackingUtility
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/*
* Type aliases provide alternative names for existing types.
If the type name is too long you can introduce a different shorter name and use the new one instead.
* instead of write MutableList<MutableList<LatLng>>
* */
typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

@AndroidEntryPoint
class TrackServices : LifecycleService() {

    private var isFirstRun = true

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    private lateinit var currNotificationBuilder: NotificationCompat.Builder

    private val timeRunInSecond = MutableLiveData<Long>()

    private var serviceKilled = false

    ////////////////////////////////////////////////////////////////////////////////////////////
    companion object {
        val isTracking = MutableLiveData<Boolean>()

        //each run list of coordinate and this list in list of runs
        val pathPoints = MutableLiveData<Polylines>()
        val timeRunInMills = MutableLiveData<Long>()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    override fun onCreate() {
        super.onCreate()
        currNotificationBuilder = baseNotificationBuilder
        postInitialValue()

        fusedLocationProviderClient = FusedLocationProviderClient(this)

        isTracking.observe(this) {
            updateLocationTracking(it)
            updateNotificationTrackingState(it)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    /*
    when we send command to our service
    */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_SERVICE -> {
                    if (isFirstRun) {
                        startForegroundService()
                        isFirstRun = false
                    } else {
                        Timber.d("Resume Service")
                        startTime()
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    Timber.d("PAUSED")
                    pauseService()
                }
                ACTION_STOP_SERVICE -> {
                    Timber.d("STOPPED")
                    killService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)

    }


    ////////////////////////////////////////////////////////////////////////////////////////////
    /*
    fun to create notification
    */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    private fun updateNotificationTrackingState(isTracking: Boolean) {
        //to change text pause or Resume
        val notificationActionText = if (isTracking) "Pause" else "Resume"
        // to send intent to notification service each intent with different request code
        val pendingIntent = if (isTracking) {
            val pauseIntent = Intent(this, TrackServices::class.java).apply {
                action = ACTION_PAUSE_SERVICE
            }
            PendingIntent.getService(
                this,
                1,
                pauseIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        } else {
            val resumeIntent = Intent(this, TrackServices::class.java).apply {
                action = ACTION_START_SERVICE
            }
            PendingIntent.getService(
                this,
                2,
                resumeIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        //to empty currNotificationBuilder before update
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        currNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(currNotificationBuilder, ArrayList<NotificationCompat.Action>())
        }
        //to change state
        if (!serviceKilled) {
            currNotificationBuilder = baseNotificationBuilder
                .addAction(R.drawable.ic_pause_black_24dp, notificationActionText, pendingIntent)
            notificationManager.notify(NOTIFICATION_ID, currNotificationBuilder.build())
        }
    }

    //  startForegroundService is service with notification
    //  to disallow to memory phone to auto close service
    private fun startForegroundService() {
        startTime()
        isTracking.postValue(true)

        //Notification
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        //create notification if version above O[26]
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }
        //build notification
        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())
        //to make stopWatch work in notification
        timeRunInSecond.observe(this) {
            if (!serviceKilled) {
                val notification = currNotificationBuilder
                    .setContentText(TrackingUtility.getFormattedStopWatchTime(it * 1000L))
                notificationManager.notify(NOTIFICATION_ID, notification.build())
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    //path initial value to live data when start service to not throw run exception
    private fun postInitialValue() {
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
        timeRunInSecond.postValue(0L)
        timeRunInMills.postValue(0L)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    //location tracking
    private fun addEmptyPolyline() = pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))


    private fun addPathPoint(location: Location?) {
        location?.let {
            val pos = LatLng(location.latitude, location.longitude)
            pathPoints.value?.apply {
                //last -> last index of the list
                last().add(pos)
                pathPoints.postValue(this)
            }
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if (isTracking.value!!) {
                result.locations.let { locations ->
                    for (location in locations) {
                        addPathPoint(location)
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking) {
            if (TrackingUtility.hasLocationPermission(this)) {
                val request = LocationRequest.create().apply {
                    interval = Location_update_Interval
                    fastestInterval = FASTET_Location_Interval
                    priority = PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //pauseService using ma app flag
    private fun pauseService() {
        isTracking.postValue(false)
        isTimeEnabled = false
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    //stop watch
    private var isTimeEnabled = false
    private var lapTime = 0L
    private var timeRun = 0L
    private var timeStarted = 0L
    private var lastSecondTimestamp = 0L

    private fun startTime() {
        //when click start add empty list of polyline and active tracking
        addEmptyPolyline()
        isTracking.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimeEnabled = true
        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!) {
                //time diff between now and time started
                lapTime = System.currentTimeMillis() - timeStarted
                //post tne new lap time
                timeRunInMills.postValue(timeRun + lapTime)
                //lastSecondTimestamp+1000L [one second]
                if (timeRunInMills.value!! >= lastSecondTimestamp + 1000L) {
                    timeRunInSecond.postValue(timeRunInSecond.value!! + 1)
                    lastSecondTimestamp += 1000L
                }
                delay(50L)
            }
            timeRun += lapTime
        }
    }

    private fun killService() {
        serviceKilled = true
        isFirstRun = true
        pauseService()
        postInitialValue()
        stopForeground(true)
        stopSelf()
    }

}