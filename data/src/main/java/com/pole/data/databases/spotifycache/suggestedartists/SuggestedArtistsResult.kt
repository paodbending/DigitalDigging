package com.pole.data.databases.spotifycache.suggestedartists

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE

@Entity(
    tableName = "suggested_artists_result",
    primaryKeys = ["seed_artist_id", "suggested_artist_id"],
    foreignKeys = [
        ForeignKey(
            entity = SuggestedArtistsRequest::class,
            parentColumns = ["seed_artist_id"],
            childColumns = ["seed_artist_id"],
            onDelete = CASCADE
        )
    ]
)
data class SuggestedArtistsResult(
    @ColumnInfo(name = "seed_artist_id") val seedArtistId: String,
    @ColumnInfo(name = "suggested_artist_id") val suggestedArtistId: String,
    val resultOrder: Int
)