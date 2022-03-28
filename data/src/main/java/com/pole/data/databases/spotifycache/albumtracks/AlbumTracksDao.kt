package com.pole.data.databases.spotifycache.albumtracks

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
internal abstract class AlbumTracksDao {

    @Query("SELECT * FROM album_tracks_request WHERE album_id = :albumId")
    abstract fun getRequest(albumId: String): Flow<AlbumTracksRequest?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun upsertRequest(requests: AlbumTracksRequest)

    @Query("DELETE FROM album_tracks_request WHERE album_id = :albumId")
    protected abstract suspend fun deleteRequest(albumId: String)

    @Query("SELECT * FROM album_tracks_result WHERE album_id = :albumId")
    abstract fun getResults(albumId: String): Flow<List<AlbumTracksResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun upsertResults(results: List<AlbumTracksResult>)

    @Transaction
    open suspend fun updateResults(
        albumId: String,
        results: List<AlbumTracksResult>
    ) {
        deleteRequest(albumId)
        upsertRequest(AlbumTracksRequest(albumId))
        upsertResults(results)
    }
}