package com.pole.domain.usecases.spotify

import com.pole.domain.Repository
import com.pole.domain.model.Track
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAlbumTracks @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(spotifyId: String): List<Track> =
        repository.getAlbumTracks(spotifyId)
}