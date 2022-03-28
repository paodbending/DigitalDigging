package com.pole.data.databases.spotifycache.suggestedartists

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pole.data.databases.spotifycache.CachedValue

@Entity(tableName = "suggested_artists_requests")
internal data class SuggestedArtistsRequest(
    @PrimaryKey @ColumnInfo(name = "seed_artist_id") val seedArtistId: String
) : CachedValue(System.currentTimeMillis())