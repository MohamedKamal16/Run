package com.example.fitness.util

import android.graphics.Color

object Constant {
    //room database name
    const val ROOM_DATABASE_NAME="RunRoom.db"
    //location permission
    const val REQUEST_CODE_LOCATION_PERMISSION=1
    //action connect fragment with server
    const val ACTION_START_SERVICE="ACTION_START_SERVICE"
    const val ACTION_PAUSE_SERVICE="ACTION_PAUSE_SERVICE"
    const val ACTION_STOP_SERVICE="ACTION_STOP_SERVICE"
    const val ACTION_SHOW_TRACKING_FRAGMENT="ACTION_SHOW_TRACKING_FRAGMENT"
    //NOTIFICATION CONNECTION
    const val NOTIFICATION_CHANNEL_ID="TRACKING_CHANNEL"
    const val NOTIFICATION_CHANNEL_NAME="TRACKING"
    const val NOTIFICATION_ID=1
    //location callback
    const val Location_update_Interval=5000L
    const val   FASTET_Location_Interval=2000L
    //polyline settings
    const val POLYLINE_COLOR=Color.RED
    const val POLYLINE_WIDTH=8f
    const val MAP_ZOOM =15f
    //SharedPreference
    const val SHARED_PREFERENCES_NAME="pref"
    const val KEY_FIRST_TIME_TOGGLE="KEY_FIRST_TIME_TOGGLE"
    const val KEY_NAME="KEY_NAME"
    const val KEY_WEIGHT="KEY_WEIGHT"
    //Dialog
    const val CANCEL_TRACKING_FRAGMENT="CANCEL_TRACKING_FRAGMENT"



}