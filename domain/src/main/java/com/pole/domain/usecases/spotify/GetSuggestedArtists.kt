package com.pole.domain.usecases.spotify

import com.pole.domain.Repository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSuggestedArtists @Inject constructor(
    private val repository: Repository
) {
    operator fun invoke(seedArtistId: String) = repository.getSuggestedArtists(seedArtistId)
}