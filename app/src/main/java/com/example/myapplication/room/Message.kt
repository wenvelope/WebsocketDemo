package com.example.myapplication.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "messages")
data class Message(
    @ColumnInfo(name = "from")
    val from:String,
    @ColumnInfo(name = "to")
    val to:String,
    @ColumnInfo(name = "message")
    val message:String,
    @ColumnInfo(name = "dateTime")
    val dateTime:LocalDateTime
){
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id:Long=0
}
