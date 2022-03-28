package com.pole.domain.usecases.userdata

import com.pole.domain.Repository
import com.pole.domain.model.spotify.SpotifyType
import com.pole.domain.model.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetUserData @Inject constructor(
    private val repository: Repository
) {
    operator fun invoke(id: String, spotifyType: SpotifyType): Flow<UserData> {
        return repository.getUserData(id, spotifyType)
    }
}