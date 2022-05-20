package com.pole.domain.usecases

import com.pole.domain.Repository
import com.pole.domain.entities.NetworkResource
import com.pole.domain.entities.Track
import kotlinx.coroutines.flow.Flow

class GetTrack(
    private val repository: Repository
) {
    operator fun invoke(spotifyId: String): Flow<NetworkResource<Track>> = repository.getTrack(spotifyId)
}