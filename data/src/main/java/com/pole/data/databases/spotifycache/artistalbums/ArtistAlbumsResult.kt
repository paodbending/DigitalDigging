package com.pole.data.databases.spotifycache.artistalbums

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE

@Entity(
    tableName = "artist_albums_result",
    primaryKeys = ["artistId", "albumId"],
    foreignKeys = [
        ForeignKey(
            entity = ArtistAlbumsRequest::class,
            parentColumns = ["artistId"],
            childColumns = ["artistId"],
            onDelete = CASCADE
        )
    ]
)
data class ArtistAlbumsResult(
    val artistId: String,
    val albumId: String
)
