package com.pole.data.databases.spotifycache.artistalbums

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pole.data.databases.spotifycache.CachedValue


@Entity(tableName = "artist_albums_request")
internal data class ArtistAlbumsRequest(
    @PrimaryKey val artistId: String,
) : CachedValue(System.currentTimeMillis())
