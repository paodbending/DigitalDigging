package com.pole.domain.usecases.userdata

import com.pole.domain.Repository
import com.pole.domain.model.SpotifyEntity
import com.pole.domain.model.SpotifyEntityUserData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataUseCase @Inject constructor(
    private val repository: Repository
) {

    suspend fun getUserData(entity: SpotifyEntity): SpotifyEntityUserData {
        return repository.getUserData(entity)
    }

    suspend fun setInLibrary(entity: SpotifyEntity, boolean: Boolean) {
        repository.setLibrary(entity, boolean)
    }

    suspend fun setScheduled(entity: SpotifyEntity, boolean: Boolean) {
        repository.setScheduled(entity, boolean)
    }
}