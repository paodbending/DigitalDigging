package com.pole.data.databases.spotifycache.track

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
internal interface TrackDao {
    @Query("SElECT * FROM track WHERE id = :id")
    fun getCachedTrack(id: String): Flow<CachedTrack?>

    @Query("SElECT * FROM track WHERE id IN (:ids)")
    fun getCachedTracks(ids: List<String>): Flow<List<CachedTrack>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCachedTrack(track: CachedTrack)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCachedTracks(tracks: List<CachedTrack>)
}