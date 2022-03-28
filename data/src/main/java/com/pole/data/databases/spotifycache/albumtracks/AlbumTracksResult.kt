package com.pole.data.databases.spotifycache.albumtracks

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE

@Entity(
    tableName = "album_tracks_result",
    primaryKeys = ["album_id", "track_id"],
    foreignKeys = [
        ForeignKey(
            entity = AlbumTracksRequest::class,
            parentColumns = ["album_id"],
            childColumns = ["album_id"],
            onDelete = CASCADE
        )
    ]
)
data class AlbumTracksResult(
    @ColumnInfo(name = "album_id") val albumId: String,
    @ColumnInfo(name = "track_id") val trackId: String,
)
