package com.pole.data.databases.userdata

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [DatabaseUserData::class],
    version = 1
)
internal abstract class UserDataDatabase : RoomDatabase() {
    abstract fun dao(): UserDataDao
}