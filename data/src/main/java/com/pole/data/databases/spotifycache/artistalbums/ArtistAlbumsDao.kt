package com.pole.data.databases.spotifycache.artistalbums

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
internal abstract class ArtistAlbumsDao {

    @Query("SELECT * FROM artist_albums_request WHERE artistId = :artistId")
    abstract fun getRequest(artistId: String): Flow<ArtistAlbumsRequest?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun upsertRequest(requests: ArtistAlbumsRequest)

    @Query("DELETE FROM artist_albums_request WHERE artistId = :artistId")
    protected abstract suspend fun deleteRequest(artistId: String)

    @Query("SELECT * FROM artist_albums_result WHERE artistId = :artistId")
    abstract fun getResults(artistId: String): Flow<List<ArtistAlbumsResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun upsertResults(results: List<ArtistAlbumsResult>)

    @Transaction
    open suspend fun updateResults(
        artistId: String,
        results: List<ArtistAlbumsResult>
    ) {
        deleteRequest(artistId)
        upsertRequest(ArtistAlbumsRequest(artistId))
        upsertResults(results)
    }
}