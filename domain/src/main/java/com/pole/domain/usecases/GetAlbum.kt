package com.pole.domain.usecases

import com.pole.domain.Repository

class GetAlbum(
    private val repository: Repository
) {
    operator fun invoke(id: String) = repository.getAlbum(id)

}