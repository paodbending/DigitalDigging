package com.pole.digitaldigging.screens.artistscreen

import com.pole.digitaldigging.UIResource
import com.pole.domain.entities.Album
import com.pole.domain.entities.Artist

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
