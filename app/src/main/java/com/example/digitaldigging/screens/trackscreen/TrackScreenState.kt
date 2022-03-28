package com.example.digitaldigging.screens.trackscreen

import com.pole.domain.model.NetworkResource
import com.pole.domain.model.UserData
import com.pole.domain.model.spotify.Album
import com.pole.domain.model.spotify.Artist
import com.pole.domain.model.spotify.Track

sealed interface TrackScreenState {
    object TrackNotFound : TrackScreenState

    object Loading : TrackScreenState

    data class Ready(
        val track: Track,
        val userData: UserData = UserData(),
        val album: NetworkResource<Album>,
        val artists: NetworkResource<List<Artist>>,
        val suggestedTracks: NetworkResource<List<Track>>
    ) : TrackScreenState
}

