package com.example.myapplication.spread

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.myapplication.TestApplication
import kotlin.reflect.KClass

fun String.showToast(context: Context=TestApplication.instance , duration:Int=Toast.LENGTH_SHORT){
    Toast.makeText(context,this,duration).show()
}

fun String.addLog(msg:String="",tag:String="wuhongru"){
    Log.e(tag,msg+this)
}

inline fun <reified T> Context.startActivity(block:Intent.() -> Unit){
    val intent = Intent(this,T::class.java)
    intent.block()
    this.startActivity(intent)
}