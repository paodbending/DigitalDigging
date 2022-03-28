package com.pole.data.databases.spotifycache.track

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pole.data.databases.spotifycache.CachedValue

@Entity(tableName = "track")
internal data class CachedTrack(
    @PrimaryKey val id: String,
    val spotifyUrl: String?,

    val name: String,
    val artistIds: String,

    val previewUrl: String? = null,

    val type: String,
    val trackNumber: Int,
    val discNumber: Int,
    val explicit: Boolean,

    val length: Int,

    val popularity: Int? = null,

    val albumId: String,
) : CachedValue(System.currentTimeMillis())