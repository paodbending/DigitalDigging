package com.pole.domain.usecases

import com.pole.domain.Repository


class GetAlbums(
    private val repository: Repository
) {
    operator fun invoke(ids: Set<String>) = repository.getAlbums(ids)
}