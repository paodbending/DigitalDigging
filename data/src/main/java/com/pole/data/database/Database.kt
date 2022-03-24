package com.pole.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [UserData::class], version = 1)
internal abstract class Database : RoomDatabase() {
    abstract fun dao(): Dao
}