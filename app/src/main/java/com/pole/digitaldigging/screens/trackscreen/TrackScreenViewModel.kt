package com.pole.digitaldigging.screens.trackscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.pole.digitaldigging.toUIResource
import com.pole.domain.entities.NetworkResource
import com.pole.domain.usecases.GetAlbum
import com.pole.domain.usecases.GetArtists
import com.pole.domain.usecases.GetSuggestedTracks
import com.pole.domain.usecases.GetTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine

class TrackScreenViewModel(
    private val getTrack: GetTrack,
    private val getArtists: GetArtists,
    private val getAlbum: GetAlbum,
    private val getSuggestedTracks: GetSuggestedTracks,
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
                                        getSuggestedTracks(trackId),
                                        getArtists(trackResource.value.artistIds.toSet()),
                                    ) { suggestedTracksResource, artistsResource ->
                                        TrackScreenState.Ready(
                                            track = trackResource.value,
                                            album = albumResource.value,
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
}