package com.pole.digitaldigging.screens.artistscreen

import com.pole.digitaldigging.UIResource
import com.pole.domain.model.spotify.Album
import com.pole.domain.model.spotify.Artist

sealed interface ArtistScreenState {

    object Loading : ArtistScreenState

    object Error : ArtistScreenState

    data class Ready(
        val artist: Artist,
        val albums: UIResource<Albums>,
    ) : ArtistScreenState {
        data class Albums(
            val albums: List<Album>,
            val singles: List<Album>,
        )
    }
}
