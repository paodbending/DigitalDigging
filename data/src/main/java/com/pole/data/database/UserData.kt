package com.pole.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pole.domain.model.SpotifyEntityType

@Entity
internal data class UserData(
    @PrimaryKey
    val id: String,
    val type: SpotifyEntityType,
    val dateAddedToLibrary: Long? = null,
    val dateAddedToSchedule: Long? = null,
)