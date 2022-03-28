package com.pole.data.databases.spotifycache.album

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pole.data.databases.spotifycache.track.CachedTrack
import kotlinx.coroutines.flow.Flow

@Dao
internal interface AlbumDao {

    @Query("SELECT * FROM album WHERE id = :id")
    fun getAlbum(id: String): Flow<CachedAlbum?>

    @Query("SELECT * FROM album WHERE id IN (:ids)")
    fun getAlbums(ids: List<String>): Flow<List<CachedAlbum>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAlbum(cachedAlbum: CachedAlbum)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAlbums(cachedAlbums: List<CachedAlbum>)
}