package com.pole.domain.usecases

import com.pole.domain.Repository

class GetArtistAlbums(
    private val repository: Repository
) {
    operator fun invoke(spotifyId: String) = repository.getArtistAlbums(spotifyId)
}