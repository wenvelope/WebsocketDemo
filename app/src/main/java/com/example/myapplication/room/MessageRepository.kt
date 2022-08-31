package com.example.myapplication.room

import androidx.lifecycle.liveData
import com.example.myapplication.spread.addLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect

class MessageRepository(private val messageDao: MessageDao) {
    fun getAllMessages() = liveData(Dispatchers.IO){
        messageDao.selectAllMessages().collect{
                "数据更新了".addLog()
                emit(it)
        }
    }
    fun getMessage(sendEmail:String)= liveData(Dispatchers.IO){
        messageDao.selectMessages(sendEmail).collect{
            emit(it)
        }
    }

    suspend fun insertMessage(message: Message){
        messageDao.insertMessage(message)
    }
}