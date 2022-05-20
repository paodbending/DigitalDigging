package com.pole.domain.usecases

import com.pole.domain.Repository

class GetArtist(
    private val repository: Repository
) {
    operator fun invoke(spotifyId: String) = repository.getArtist(spotifyId)
}