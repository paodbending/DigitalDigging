package com.pole.domain.usecases.userdata

import com.pole.domain.Repository
import com.pole.domain.model.spotify.SpotifyType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlipUserDataScheduled @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(id: String, type: SpotifyType) = repository.flipScheduled(id, type)
}