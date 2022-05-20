package com.pole.domain.usecases

import com.pole.domain.Repository

class GetTracks(
    private val repository: Repository
) {
    operator fun invoke(ids: Set<String>) = repository.getTracks(ids)
}