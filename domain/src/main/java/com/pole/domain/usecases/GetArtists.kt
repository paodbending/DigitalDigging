package com.pole.domain.usecases

import com.pole.domain.Repository

class GetArtists(
    private val repository: Repository
) {
    operator fun invoke(ids: Set<String>) = repository.getArtists(ids)

}