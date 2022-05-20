package com.pole.domain.usecases

import com.pole.domain.Repository

class GetAlbumTracks(
    private val repository: Repository
) {
    operator fun invoke(spotifyId: String) = repository.getAlbumTracks(spotifyId)
}