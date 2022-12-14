package com.example.myapplication.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Insert
    fun insertMessage(message:Message)

    @Query("select * from messages order by dateTime(dateTime)")
    fun selectAllMessages():Flow<List<Message>>
    @Query("select * from messages where `from` = (:sendEmail) or `to` =(:sendEmail)  order by dateTime(dateTime)")
    fun selectMessages(sendEmail:String):Flow<List<Message>>
}