package com.pole.domain.usecases

import com.pole.domain.Repository
import com.pole.domain.model.Album
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetArtistAlbums @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(spotifyId: String): List<Album> =
        repository.getArtistAlbums(spotifyId)
}