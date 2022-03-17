package com.pole.domain.usecases

import com.pole.domain.Repository
import com.pole.domain.model.ArtistInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetArtistInfo @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(spotifyId: String): ArtistInfo? =
        repository.getArtistInfo(spotifyId)
}