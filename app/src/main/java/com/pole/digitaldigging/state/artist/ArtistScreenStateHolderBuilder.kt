package com.pole.digitaldigging.state.artist

import com.pole.digitaldigging.state.artist.intent.OnBackPressIntent
import com.pole.domain.usecases.GetArtist
import com.pole.domain.usecases.GetArtistAlbums
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArtistScreenStateHolderBuilder @Inject constructor(
    private val getArtistAlbums: GetArtistAlbums,
    private val getArtist: GetArtist,
) {
    fun build(artistId: String, onBackPressDelegate: OnBackPressIntent): ArtistScreenStateHolder =
        ArtistScreenStateHolder(
            artistId = artistId,
            getArtist = getArtist,
            getArtistAlbums = getArtistAlbums,
            onBackPressDelegate = onBackPressDelegate
        )
}