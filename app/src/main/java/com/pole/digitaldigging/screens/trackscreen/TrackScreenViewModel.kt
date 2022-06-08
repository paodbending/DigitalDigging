package com.pole.digitaldigging.screens.trackscreen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.pole.digitaldigging.toUIResource
import com.pole.domain.entities.NetworkResource
import com.pole.domain.usecases.GetAlbum
import com.pole.domain.usecases.GetArtists
import com.pole.domain.usecases.GetSuggestedTracks
import com.pole.domain.usecases.GetTrack
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class TrackScreenViewModel @Inject constructor(
    private val getTrack: GetTrack,
    private val getArtists: GetArtists,
    private val getAlbum: GetAlbum,
    private val getSuggestedTracks: GetSuggestedTracks,
) : ViewModel() {

    private val mutableState: MutableState<TrackScreenState> =
        mutableStateOf(TrackScreenState.Loading)
    val state: State<TrackScreenState> = mutableState

    suspend fun collectState(trackId: String) {
        getTrack(trackId).collectLatest { trackResource ->
            when (trackResource) {
                is NetworkResource.Error -> mutableState.value = TrackScreenState.Error
                is NetworkResource.Loading -> mutableState.value = TrackScreenState.Loading
                is NetworkResource.Ready -> {
                    getAlbum(trackResource.value.albumId).collectLatest { albumResource ->
                        when (albumResource) {
                            is NetworkResource.Error -> mutableState.value = TrackScreenState.Error
                            is NetworkResource.Loading -> mutableState.value =
                                TrackScreenState.Loading
                            is NetworkResource.Ready -> {
                                combine(
                                    getSuggestedTracks(trackId),
                                    getArtists(trackResource.value.artistIds.toSet()),
                                ) { suggestedTracksResource, artistsResource ->
                                    TrackScreenState.Ready(
                                        track = trackResource.value,
                                        album = albumResource.value,
                                        suggestedTracks = suggestedTracksResource.toUIResource(),
                                        artists = artistsResource.toUIResource(),
                                    )
                                }.collect { mutableState.value = it }
                            }
                        }
                    }
                }
            }
        }
    }
}