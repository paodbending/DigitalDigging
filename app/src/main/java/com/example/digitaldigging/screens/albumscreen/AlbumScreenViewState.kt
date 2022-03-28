package com.example.digitaldigging.screens.albumscreen

import com.pole.domain.model.UserData
import com.pole.domain.model.spotify.Album
import com.pole.domain.model.spotify.Track

sealed interface AlbumScreenState {
    object Loading : AlbumScreenState

    object AlbumNotFound : AlbumScreenState

    data class Ready(
        val album: Album,
        val tracks: List<Track>,
        val userData: UserData,
    ) : AlbumScreenState
}
