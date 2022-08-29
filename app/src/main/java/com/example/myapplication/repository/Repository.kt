package com.example.myapplication.repository

import androidx.lifecycle.liveData
import com.example.myapplication.network.UserNetWork
import com.example.myapplication.spread.addLog
import kotlinx.coroutines.Dispatchers
import java.lang.Exception

object Repository {

    fun getUserId(name:String,pwd:String) = liveData<Result<String>>(Dispatchers.IO) {
        val result = try {
            val responseBody = UserNetWork.getUserId(name,pwd).string()
            if(responseBody!="error"){
                Result.success(responseBody)
            }else{
                Result.success("error")
            }
        }catch (e:Exception){
            Result.failure(e)
        }
        emit(result)
    }
}