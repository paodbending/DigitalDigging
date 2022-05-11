package com.example.digitaldigging.screens.albumscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.digitaldigging.UIResource
import com.example.digitaldigging.toUIResource
import com.pole.domain.model.NetworkResource
import com.pole.domain.model.spotify.SpotifyType
import com.pole.domain.usecases.spotify.GetAlbum
import com.pole.domain.usecases.spotify.GetAlbumTracks
import com.pole.domain.usecases.spotify.GetArtists
import com.pole.domain.usecases.userdata.FlipUserDataLibrary
import com.pole.domain.usecases.userdata.FlipUserDataScheduled
import com.pole.domain.usecases.userdata.GetUserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumScreenViewModel @Inject constructor(
    private val getAlbum: GetAlbum,
    private val getAlbumTracks: GetAlbumTracks,
    getUserData: GetUserData,
    private val getArtists: GetArtists,
    private val flipUserDataScheduled: FlipUserDataScheduled,
    private val flipUserDataLibrary: FlipUserDataLibrary,
) : ViewModel() {

    private val albumIdFlow = MutableStateFlow("")

    val state: LiveData<AlbumScreenState> = liveData(Dispatchers.Default) {
        albumIdFlow.collectLatest { albumId ->

            emit(AlbumScreenState.Loading)

            getAlbum(albumId).collectLatest { albumResource ->
                getAlbumTracks(albumId).collectLatest { tracksResource ->
                    getUserData(albumId, SpotifyType.ALBUM).collectLatest { userData ->
                        when (albumResource) {
                            is NetworkResource.Error -> emit(AlbumScreenState.Error)
                            is NetworkResource.Loading -> emit(AlbumScreenState.Loading)
                            is NetworkResource.Ready -> {
                                getArtists(albumResource.value.artistIds.toSet()).collectLatest { artistsResource ->
                                    emit(AlbumScreenState.Ready(
                                        album = albumResource.value,
                                        userData = userData,
                                        tracks = when (tracksResource) {
                                            is NetworkResource.Ready -> UIResource.Ready(
                                                tracksResource.value.sortedBy { it.trackNumber })
                                            else -> tracksResource.toUIResource()
                                        },
                                        artists = when (artistsResource) {
                                            is NetworkResource.Ready -> UIResource.Ready(
                                                artistsResource.value)
                                            else -> artistsResource.toUIResource()
                                        }
                                    ))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun setAlbumId(id: String) {
        albumIdFlow.value = id
    }

    fun flipLibrary() {
        val currentState = state.value
        if (currentState is AlbumScreenState.Ready) {
            viewModelScope.launch {
                flipUserDataLibrary(currentState.album.id, SpotifyType.ARTIST)
            }
        }
    }

    fun flipSchedule() {
        val currentState = state.value
        if (currentState is AlbumScreenState.Ready) {
            viewModelScope.launch {
                flipUserDataScheduled(currentState.album.id, SpotifyType.ARTIST)
            }
        }
    }
}
