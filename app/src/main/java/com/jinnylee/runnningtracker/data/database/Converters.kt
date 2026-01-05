package com.jinnylee.runnningtracker.data.database

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class Converters {

    // 비트맵 -> ByteArray (DB 저장용)
    @TypeConverter
    fun fromBitmap(bmp: Bitmap?): ByteArray? {
        val outputStream = ByteArrayOutputStream()
        bmp?.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    // ByteArray -> 비트맵 (화면 표시용)
    @TypeConverter
    fun toBitmap(bytes: ByteArray?): Bitmap? {
        return if (bytes != null) {
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } else {
            null
        }
    }
}