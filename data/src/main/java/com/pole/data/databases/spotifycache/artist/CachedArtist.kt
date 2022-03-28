package com.pole.data.databases.spotifycache.artist

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pole.data.databases.spotifycache.CachedValue

@Entity(tableName = "artist")
internal data class CachedArtist(
    @PrimaryKey val id: String,
    val spotifyUrl: String?,
    val name: String,
    val type: String,
    val imageUrl: String?,
    val followers: Int? = null,
    val genres: String,
    val popularity: Int,
) : CachedValue(System.currentTimeMillis())