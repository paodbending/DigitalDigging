package com.example.digitaldigging.screens.albuminfo

import com.pole.domain.model.AlbumInfo
import com.pole.domain.model.Track

sealed interface AlbumInfoViewState

object Loading : AlbumInfoViewState

object AlbumNotFound : AlbumInfoViewState

data class Ready(
    val albumInfo: AlbumInfo,
    val tracks: List<Track>
) : AlbumInfoViewState