package com.pole.domain.usecases

import com.pole.domain.Repository

class GetSuggestedArtists(
    private val repository: Repository
) {
    operator fun invoke(seedArtistId: String) = repository.getSuggestedArtists(seedArtistId)
}