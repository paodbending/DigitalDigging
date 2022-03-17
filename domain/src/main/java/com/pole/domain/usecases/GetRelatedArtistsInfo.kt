package com.pole.domain.usecases

import com.pole.domain.Repository
import com.pole.domain.model.ArtistInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetRelatedArtistsInfo @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(spotifyId: String): List<ArtistInfo> =
        repository.getRelatedArtistsInfo(spotifyId)
}