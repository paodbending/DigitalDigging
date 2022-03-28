package com.pole.data.databases.userdata

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_data")
internal data class DatabaseUserData(
    @PrimaryKey
    val id: String,
    val type: String,
    val dateAddedToLibrary: Long? = null,
    val dateAddedToSchedule: Long? = null,
)