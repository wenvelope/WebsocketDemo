package com.example.myapplication.room

import android.util.Log
import androidx.lifecycle.liveData
import com.example.myapplication.TestApplication
import com.example.myapplication.spread.addLog
import com.example.myapplication.spread.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart


class UserRepository(private val userDao:UserDao) {

    companion object{
        const val TAG = "wuHongRu"
    }

    //可变参数 本质为数组
    fun insertUsers(vararg user:Users){
        userDao.insertUser(*user)
    }

    fun loadAllUser():List<Users>{
        return userDao.loadAllUsers()
    }

    fun getUser(name:String)= liveData<Users>(Dispatchers.IO){
           userDao.fetchUserInfo(name).collect{
               it.size.toString().addLog("size:")
               it.forEach { it->
                   emit(it)
                   it.id.toString().addLog()
               }
           }
    }
}