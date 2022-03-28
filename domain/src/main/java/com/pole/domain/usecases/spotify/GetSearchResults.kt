package com.pole.domain.usecases.spotify

import com.pole.domain.Repository
import com.pole.domain.model.NetworkResource
import com.pole.domain.model.spotify.SearchResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSearchResults @Inject constructor(
    private val repository: Repository
) {
    operator fun invoke(query: String): Flow<NetworkResource<SearchResult>> {
        return repository.getSearchResults(query)
    }
}