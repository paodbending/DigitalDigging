package com.pole.domain.usecases

import com.pole.domain.Repository
import com.pole.domain.model.NetworkResource
import com.pole.domain.model.spotify.Track
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetTrack @Inject constructor(
    private val repository: Repository
) {
    operator fun invoke(spotifyId: String): Flow<NetworkResource<Track>> = repository.getTrack(spotifyId)
}