package com.pole.domain.usecases

import com.pole.domain.Repository
import com.pole.domain.model.TrackInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetTrackInfo @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(spotifyId: String): TrackInfo? = repository.getTrackInfo(spotifyId)
}