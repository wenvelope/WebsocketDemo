package com.example.myapplication

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class TestApplication:Application() {

    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var instance:Context
    }

    override fun onCreate() {
        super.onCreate()
        instance = applicationContext
    }
}