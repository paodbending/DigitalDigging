package com.example.digitaldigging.screens.artistinfo

import com.pole.domain.model.Album
import com.pole.domain.model.ArtistInfo

sealed interface ViewState

object ArtistNotFound : ViewState

object Loading : ViewState

data class Ready(
    val artistInfo: ArtistInfo,
    val albums: List<Album>
) : ViewState