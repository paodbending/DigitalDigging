package com.pole.data.databases.spotifycache.albumtracks

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pole.data.databases.spotifycache.CachedValue

@Entity(tableName = "album_tracks_request")
internal data class AlbumTracksRequest(
    @PrimaryKey @ColumnInfo(name = "album_id") val albumId: String
) : CachedValue(System.currentTimeMillis())
