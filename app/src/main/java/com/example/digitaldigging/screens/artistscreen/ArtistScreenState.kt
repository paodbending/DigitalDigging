package com.example.digitaldigging.screens.artistscreen

import com.pole.domain.model.NetworkResource
import com.pole.domain.model.UserData
import com.pole.domain.model.spotify.Album
import com.pole.domain.model.spotify.Artist

sealed interface ArtistScreenState {

    object Loading : ArtistScreenState

    object ArtistNotFound : ArtistScreenState

    data class Ready(
        val artist: Artist,
        val userData: UserData,
        val albums: NetworkResource<ArtistAlbums>,
    ) : ArtistScreenState {
        data class ArtistAlbums(
            val albums: List<Album>,
            val singles: List<Album>,
        )
    }
}

