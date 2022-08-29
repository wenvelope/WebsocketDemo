package com.example.myapplication.room

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@RequiresApi(Build.VERSION_CODES.O)
@Database(version = 2, entities = [Users::class,Message::class],exportSchema = false)
@TypeConverters(DbTypeConverters::class)
abstract class UserDatabase:RoomDatabase() {

    abstract fun userDao():UserDao
    abstract fun MessageDao():MessageDao

    companion object{
        private var instance:UserDatabase ?=null
        @Synchronized
        fun getDataBase(context:Context):UserDatabase{
            instance?.let { return it }
            return Room.databaseBuilder(context.applicationContext,UserDatabase::class.java,"user_database")
                .fallbackToDestructiveMigration()
                .build().apply {
                    instance = this
                }
        }
    }
}