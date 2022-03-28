package com.pole.data.databases.spotifycache.suggetedtracks

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE

@Entity(
    tableName = "suggested_tracks_result",
    primaryKeys = ["seed_track_id", "suggested_track_id"],
    foreignKeys = [
        ForeignKey(
            entity = SuggestedTracksRequest::class,
            parentColumns = ["seed_track_id"],
            childColumns = ["seed_track_id"],
            onDelete = CASCADE
        )
    ]
)
internal data class SuggestedTracksResult(
    @ColumnInfo(name = "seed_track_id") val seedTrackId: String,
    @ColumnInfo(name = "suggested_track_id") val suggestedTrackId: String,
    val resultOrder: Int
)
