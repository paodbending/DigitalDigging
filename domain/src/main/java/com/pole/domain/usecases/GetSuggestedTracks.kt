package com.pole.domain.usecases

import com.pole.domain.Repository

class GetSuggestedTracks(
    private val repository: Repository
) {
    operator fun invoke(seedTrackId: String) = repository.getSuggestedTracks(seedTrackId)
}