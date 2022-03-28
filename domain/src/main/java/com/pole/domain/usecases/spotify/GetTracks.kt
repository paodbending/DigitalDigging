package com.pole.domain.usecases.spotify

import com.pole.domain.Repository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetTracks @Inject constructor(
    private val repository: Repository
) {
    operator fun invoke(ids: Set<String>) = repository.getTracks(ids)
}