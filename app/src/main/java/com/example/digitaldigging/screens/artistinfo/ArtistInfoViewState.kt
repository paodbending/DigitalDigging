package com.example.digitaldigging.screens.artistinfo

import com.pole.domain.model.Album
import com.pole.domain.model.ArtistInfo

sealed interface ArtistInfoViewState

object ArtistNotFound : ArtistInfoViewState

object Loading : ArtistInfoViewState

data class Ready(
    val artistInfo: ArtistInfo,
    val albums: List<Album>,
    val singles: List<Album>,
    val appearsOn: List<Album>,
    val compilations: List<Album>
) : ArtistInfoViewState