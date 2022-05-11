package com.example.digitaldigging.screens.trackscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.digitaldigging.toUIResource
import com.pole.domain.model.NetworkResource
import com.pole.domain.model.spotify.SpotifyType
import com.pole.domain.usecases.spotify.GetAlbum
import com.pole.domain.usecases.spotify.GetArtists
import com.pole.domain.usecases.spotify.GetSuggestedTracks
import com.pole.domain.usecases.spotify.GetTrack
import com.pole.domain.usecases.userdata.FlipUserDataLibrary
import com.pole.domain.usecases.userdata.FlipUserDataScheduled
import com.pole.domain.usecases.userdata.GetUserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackScreenViewModel @Inject constructor(
    private val getTrack: GetTrack,
    private val getUserData: GetUserData,
    private val getArtists: GetArtists,
    private val getAlbum: GetAlbum,
    private val getSuggestedTracks: GetSuggestedTracks,
    private val flipUserDataLibrary: FlipUserDataLibrary,
    private val flipUserDataScheduled: FlipUserDataScheduled,
) : ViewModel() {

    private val trackIdFlow = MutableStateFlow("")

    val state: LiveData<TrackScreenState> = liveData(Dispatchers.Default) {
        trackIdFlow.collectLatest { trackId ->

            emit(TrackScreenState.Loading)

            getTrack(trackId).collectLatest { trackResource ->
                when (trackResource) {
                    is NetworkResource.Error -> emit(TrackScreenState.Error)
                    is NetworkResource.Loading -> emit(TrackScreenState.Loading)
                    is NetworkResource.Ready -> {
                        getAlbum(trackResource.value.albumId).collectLatest { albumResource ->
                            when (albumResource) {
                                is NetworkResource.Error -> emit(TrackScreenState.Error)
                                is NetworkResource.Loading -> emit(TrackScreenState.Loading)
                                is NetworkResource.Ready -> {
                                    combine(
                                        getUserData(trackId, SpotifyType.TRACK),
                                        getSuggestedTracks(trackId),
                                        getArtists(trackResource.value.artistIds.toSet()),
                                    ) { userData, suggestedTracksResource, artistsResource ->
                                        TrackScreenState.Ready(
                                            track = trackResource.value,
                                            album = albumResource.value,
                                            userData = userData,
                                            suggestedTracks = suggestedTracksResource.toUIResource(),
                                            artists = artistsResource.toUIResource(),
                                        )
                                    }.collect { emit(it) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun setTrackId(id: String) {
        trackIdFlow.value = id
    }

    fun flipLibrary() {
        val currentState = state.value
        if (currentState is TrackScreenState.Ready) {
            viewModelScope.launch {
                flipUserDataLibrary(currentState.track.id, SpotifyType.TRACK)
            }
        }
    }

    fun flipSchedule() {
        val currentState = state.value
        if (currentState is TrackScreenState.Ready) {
            viewModelScope.launch {
                flipUserDataScheduled(currentState.track.id, SpotifyType.TRACK)
            }
        }
    }
}