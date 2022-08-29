package com.example.myapplication.room

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
@RequiresApi(Build.VERSION_CODES.O)

class DbTypeConverters {
    companion object {
        @TypeConverter
        @JvmStatic
         fun getDateTimeOfTimestamp(timestamp: Long): LocalDateTime {
            val instant: Instant = Instant.ofEpochMilli(timestamp)
            val zone: ZoneId = ZoneId.systemDefault()
            return LocalDateTime.ofInstant(instant , zone)
        }


        @TypeConverter
        @JvmStatic
         fun getTimestampOfDateTime(localDateTime: LocalDateTime): Long {
            val zone = ZoneId.systemDefault()
            val instant = localDateTime.atZone(zone).toInstant()
            return instant.toEpochMilli()
        }
    }

}