package com.pole.data.databases.spotifycache.track

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
internal interface TrackDao {
    @Query("SElECT * FROM track WHERE id = :id")
    fun getTrack(id: String): Flow<CachedTrack?>

    @Query("SElECT * FROM track WHERE id IN (:ids)")
    fun getTracks(ids: List<String>): Flow<List<CachedTrack>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTrack(track: CachedTrack)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTracks(tracks: List<CachedTrack>)
}