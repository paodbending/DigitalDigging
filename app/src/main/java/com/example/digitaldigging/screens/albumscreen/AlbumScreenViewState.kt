package com.example.digitaldigging.screens.albumscreen

import com.example.digitaldigging.UIResource
import com.pole.domain.model.UserData
import com.pole.domain.model.spotify.Album
import com.pole.domain.model.spotify.Artist
import com.pole.domain.model.spotify.Track

sealed interface AlbumScreenState {
    object Loading : AlbumScreenState

    object Error : AlbumScreenState

    data class Ready(
        val album: Album,
        val tracks: UIResource<List<Track>>,
        val artists: UIResource<List<Artist>>,
        val userData: UserData,
    ) : AlbumScreenState
}
