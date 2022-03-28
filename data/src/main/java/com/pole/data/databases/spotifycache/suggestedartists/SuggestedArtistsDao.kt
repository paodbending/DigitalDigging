package com.pole.data.databases.spotifycache.suggestedartists

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
internal abstract class SuggestedArtistsDao {
    @Query("SELECT * FROM suggested_artists_requests WHERE seed_artist_id =:seedTrackId")
    abstract fun getRequest(seedTrackId: String): Flow<SuggestedArtistsRequest?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun upsertRequest(request: SuggestedArtistsRequest)

    @Query("DELETE FROM suggested_artists_requests WHERE seed_artist_id = :seedTrackId")
    protected abstract suspend fun deleteRequest(seedTrackId: String)

    @Query("SELECT * FROM suggested_artists_result WHERE seed_artist_id = :seedTrackId ORDER BY resultOrder ASC")
    abstract fun getResults(seedTrackId: String): Flow<List<SuggestedArtistsResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun upsertResults(results: List<SuggestedArtistsResult>)

    @Transaction
    open suspend fun updateResults(
        seedArtistId: String,
        results: List<SuggestedArtistsResult>
    ) {
        deleteRequest(seedArtistId)
        upsertRequest(SuggestedArtistsRequest(seedArtistId))
        upsertResults(results)
    }
}