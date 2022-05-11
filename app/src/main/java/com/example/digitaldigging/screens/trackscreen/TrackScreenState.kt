package com.example.digitaldigging.screens.trackscreen

import com.example.digitaldigging.UIResource
import com.pole.domain.model.UserData
import com.pole.domain.model.spotify.Album
import com.pole.domain.model.spotify.Artist
import com.pole.domain.model.spotify.Track

sealed interface TrackScreenState {
    object Error : TrackScreenState

    object Loading : TrackScreenState

    data class Ready(
        val track: Track,
        val userData: UserData = UserData(),
        val album: Album,
        val artists: UIResource<List<Artist>>,
        val suggestedTracks: UIResource<List<Track>>
    ) : TrackScreenState
}