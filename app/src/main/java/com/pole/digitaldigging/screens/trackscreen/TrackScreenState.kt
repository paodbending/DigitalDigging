package com.pole.digitaldigging.screens.trackscreen

import com.pole.digitaldigging.UIResource
import com.pole.domain.entities.Album
import com.pole.domain.entities.Artist
import com.pole.domain.entities.Track

sealed interface TrackScreenState {
    object Error : TrackScreenState

    object Loading : TrackScreenState

    data class Ready(
        val track: Track,
        val album: Album,
        val artists: UIResource<List<Artist>>,
        val suggestedTracks: UIResource<List<Track>>
    ) : TrackScreenState
}