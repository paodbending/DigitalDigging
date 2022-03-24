package com.pole.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
internal interface Dao {

    @Query("SELECT * FROM userdata WHERE id = :id")
    suspend fun getUserData(id: String): UserData?

    @Query("SELECT * FROM userdata WHERE id = :id")
    suspend fun getUserDataFlow(id: String): UserData?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateUserData(userData: UserData)
}