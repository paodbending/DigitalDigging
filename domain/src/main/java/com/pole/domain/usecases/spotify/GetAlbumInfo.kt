package com.pole.domain.usecases.spotify

import com.pole.domain.Repository
import com.pole.domain.model.AlbumInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAlbumInfo @Inject constructor(
    private val repository: Repository
) {

    suspend operator fun invoke(spotifyId: String): AlbumInfo? {
        return repository.getAlbumInfo(spotifyId)
    }
}