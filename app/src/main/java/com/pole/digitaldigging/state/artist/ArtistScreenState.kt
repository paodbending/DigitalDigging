package com.pole.digitaldigging.state.artist

import com.pole.digitaldigging.UIResource
import com.pole.digitaldigging.state.artist.intent.OnBackPressIntent
import com.pole.domain.entities.Album
import com.pole.domain.entities.Artist

sealed interface ArtistScreenState : OnBackPressIntent {

    data class Loading(
        private val onBackPressDelegate: OnBackPressIntent,
    ) : ArtistScreenState,
        OnBackPressIntent by onBackPressDelegate

    data class Error(
        private val onBackPressDelegate: OnBackPressIntent,
    ) : ArtistScreenState,
        OnBackPressIntent by onBackPressDelegate

    data class Ready(
        val artist: Artist,
        val albums: UIResource<Albums>,
        private val onBackPressDelegate: OnBackPressIntent,
    ) : ArtistScreenState,
        OnBackPressIntent by onBackPressDelegate {
        data class Albums(
            val albums: List<Album>,
            val singles: List<Album>,
        )
    }
}