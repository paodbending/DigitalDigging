package com.example.digitaldigging.screens.trackscreen

import androidx.lifecycle.*
import com.pole.domain.model.NetworkResource
import com.pole.domain.model.spotify.SpotifyType
import com.pole.domain.usecases.spotify.GetAlbum
import com.pole.domain.usecases.spotify.GetArtists
import com.pole.domain.usecases.spotify.GetTrack
import com.pole.domain.usecases.spotify.GetSuggestedTracks
import com.pole.domain.usecases.userdata.FlipUserDataLibrary
import com.pole.domain.usecases.userdata.FlipUserDataScheduled
import com.pole.domain.usecases.userdata.GetUserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.transform
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
    private val flipUserDataScheduled: FlipUserDataScheduled
) : ViewModel() {

    private val trackId = MutableLiveData<String>()

    val state: LiveData<TrackScreenState> = trackId.distinctUntilChanged().switchMap { trackId ->
        liveData(Dispatchers.Default) {

            emit(TrackScreenState.Loading)

            getTrack(trackId).transform { trackResource ->
                when (trackResource) {
                    is NetworkResource.Error -> this@liveData.emit(TrackScreenState.TrackNotFound)
                    is NetworkResource.Loading -> this@liveData.emit(TrackScreenState.Loading)
                    is NetworkResource.Ready -> emit(trackResource.value)
                }
            }.collect { track ->
                combine(
                    getUserData(trackId, SpotifyType.TRACK),
                    getSuggestedTracks(trackId),
                    getArtists(track.artistIds.toSet()),
                    getAlbum(track.albumId)
                ) { userData, suggestedTracksResource, artistsResource, albumResource ->
                    TrackScreenState.Ready(
                        track = track,
                        userData = userData,
                        suggestedTracks = suggestedTracksResource,
                        artists = artistsResource,
                        album = albumResource
                    )
                }.collect { emit(it) }
            }
        }
    }

    fun setTrackId(id: String) {
        trackId.value = id
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