package com.pole.data.databases.userdata

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
internal interface UserDataDao {
    @Query("SELECT * FROM user_data WHERE id = :id")
    fun getUserData(id: String): Flow<DatabaseUserData?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUserData(databaseUserData: DatabaseUserData)
}