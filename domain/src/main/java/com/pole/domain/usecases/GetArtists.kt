package com.pole.domain.usecases

import com.pole.domain.Repository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetArtists @Inject constructor(
    private val repository: Repository
) {
    operator fun invoke(ids: Set<String>) = repository.getArtists(ids)

}