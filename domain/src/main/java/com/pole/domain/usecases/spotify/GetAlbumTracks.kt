package com.pole.domain.usecases.spotify

import com.pole.domain.Repository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAlbumTracks @Inject constructor(
    private val repository: Repository
) {
    operator fun invoke(spotifyId: String) = repository.getAlbumTracks(spotifyId)
}