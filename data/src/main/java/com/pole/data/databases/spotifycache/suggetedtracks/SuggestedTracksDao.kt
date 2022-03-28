package com.pole.data.databases.spotifycache.suggetedtracks

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
internal abstract class SuggestedTracksDao {

    @Query("SELECT * FROM suggested_track_request WHERE seed_track_id =:seedTrackId")
    abstract fun getRequest(seedTrackId: String): Flow<SuggestedTracksRequest?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun upsertRequest(request: SuggestedTracksRequest)

    @Query("DELETE FROM suggested_track_request WHERE seed_track_id = :seedTrackId")
    protected abstract suspend fun deleteRequest(seedTrackId: String)

    @Query("SELECT * FROM suggested_tracks_result WHERE seed_track_id = :seedTrackId ORDER BY resultOrder ASC")
    abstract fun getResults(seedTrackId: String): Flow<List<SuggestedTracksResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun upsertResults(results: List<SuggestedTracksResult>)

    @Transaction
    open suspend fun updateResults(
        seedTrackId: String,
        results: List<SuggestedTracksResult>
    ) {
        deleteRequest(seedTrackId)
        upsertRequest(SuggestedTracksRequest(seedTrackId))
        upsertResults(results)
    }
}