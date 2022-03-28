package com.pole.data.databases.spotifycache.suggetedtracks

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pole.data.databases.spotifycache.CachedValue

@Entity(tableName = "suggested_track_request")
internal data class SuggestedTracksRequest(
    @PrimaryKey @ColumnInfo(name = "seed_track_id") val seedTrackId: String
) : CachedValue(System.currentTimeMillis())