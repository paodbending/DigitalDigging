package com.pole.domain.usecases

import com.pole.domain.Repository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAlbum @Inject constructor(
    private val repository: Repository
) {
    operator fun invoke(id: String) = repository.getAlbum(id)

}