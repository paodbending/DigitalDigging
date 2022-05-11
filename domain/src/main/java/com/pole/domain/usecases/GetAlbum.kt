package com.pole.domain.usecases

import com.pole.domain.Repository
import com.pole.domain.model.NetworkResource
import com.pole.domain.model.spotify.Album
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAlbum @Inject constructor(
    private val repository: Repository
) {
    operator fun invoke(id: String) = repository.getAlbum(id)

}