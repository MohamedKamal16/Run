package com.example.fitness.model.db

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class Convertors {
    @TypeConverter
    fun fromBitmapToByte(bitmap: Bitmap):ByteArray{
        val outputStream=ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream)
        return  outputStream.toByteArray()
    }
    @TypeConverter
    fun toBitMap(byteArray: ByteArray):Bitmap{
        return BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
    }
}