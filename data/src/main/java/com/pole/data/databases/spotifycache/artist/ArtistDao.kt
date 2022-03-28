package com.pole.data.databases.spotifycache.artist

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pole.data.databases.spotifycache.artistalbums.ArtistAlbumsRequest
import kotlinx.coroutines.flow.Flow

@Dao
internal interface ArtistDao {
    @Query("SELECT * FROM artist WHERE id = :id")
    fun getArtist(id: String): Flow<CachedArtist?>

    @Query("SELECT * FROM artist WHERE id IN (:ids)")
    fun getArtists(ids: List<String>): Flow<List<CachedArtist>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertArtist(artist: CachedArtist)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertArtists(artist: List<CachedArtist>)
}