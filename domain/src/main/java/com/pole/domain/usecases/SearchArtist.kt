package com.pole.domain.usecases

import com.pole.domain.Repository
import com.pole.domain.model.ArtistInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchArtist @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(query: String): List<ArtistInfo> = repository.searchArtist(query)
}