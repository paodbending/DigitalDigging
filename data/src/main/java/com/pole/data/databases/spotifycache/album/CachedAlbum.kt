package com.pole.data.databases.spotifycache.album

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pole.data.databases.spotifycache.CachedValue

@Entity(tableName = "album")
internal data class CachedAlbum(
    @PrimaryKey val id: String,
    val spotifyUrl: String?,
    val albumType: String,
    val artistIds: String,
    val artistNames: String,
    val name: String,
    val type: String,
    val imageUrl: String?,
    val totalTracks: Int? = null,
    val releaseDateYear: Int,
    val releaseDateMonth: Int?,
    val releaseDateDay: Int?,
    val genres: String,
    val label: String,
    val popularity: Int
) : CachedValue(System.currentTimeMillis())
