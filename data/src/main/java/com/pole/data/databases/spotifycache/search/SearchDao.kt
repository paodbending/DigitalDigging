package com.pole.data.databases.spotifycache.search

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
internal abstract class SearchDao {

    @Query("SELECT * FROM search_request WHERE searchQuery =:searchQuery")
    abstract fun getRequest(searchQuery: String): Flow<SearchRequest?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun upsertRequest(request: SearchRequest)

    @Query("DELETE FROM search_request WHERE searchQuery = :searchQuery")
    protected abstract suspend fun deleteRequest(searchQuery: String)

    @Query("SELECT * FROM search_result WHERE searchQuery = :searchQuery ORDER BY resultOrder ASC")
    abstract fun getResults(searchQuery: String): Flow<List<CachedSearchResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun upsertResults(results: List<CachedSearchResult>)

    @Transaction
    open suspend fun updateResults(
        searchQuery: String,
        results: List<CachedSearchResult>
    ) {
        deleteRequest(searchQuery)
        upsertRequest(SearchRequest(searchQuery))
        upsertResults(results)
    }
}