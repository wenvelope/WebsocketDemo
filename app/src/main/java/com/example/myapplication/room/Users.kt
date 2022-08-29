package com.example.myapplication.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class Users(
    @ColumnInfo(name = "user", defaultValue = "wuHongRu")
    var userName: String ,
    @ColumnInfo
    var password: String
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id:Long = 0
}
