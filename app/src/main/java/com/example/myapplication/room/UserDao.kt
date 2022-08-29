package com.example.myapplication.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = Users::class)
    fun insertUser(vararg user:Users)

    @Query("select * from user ")
    fun loadAllUsers():List<Users>

    @Query("select * from user where user = :name")
    fun fetchUserInfo(name:String):Flow<List<Users>>

}