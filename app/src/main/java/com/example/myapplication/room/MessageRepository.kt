package com.example.myapplication.room

import androidx.lifecycle.liveData
import com.example.myapplication.spread.addLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect

class MessageRepository(private val messageDao: MessageDao) {
    fun getMessages() = liveData(Dispatchers.IO){
        messageDao.selectAllMessages().collect{
                "数据更新了".addLog()
                emit(it)
        }
    }

    suspend fun insertMessage(message: Message){
        messageDao.insertMessage(message)
    }
}