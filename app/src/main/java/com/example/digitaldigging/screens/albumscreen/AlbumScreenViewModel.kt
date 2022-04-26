package com.example.digitaldigging.screens.albumscreen

import androidx.lifecycle.*
import com.pole.domain.model.NetworkResource
import com.pole.domain.model.spotify.SpotifyType
import com.pole.domain.usecases.spotify.GetAlbum
import com.pole.domain.usecases.spotify.GetAlbumTracks
import com.pole.domain.usecases.userdata.FlipUserDataLibrary
import com.pole.domain.usecases.userdata.FlipUserDataScheduled
import com.pole.domain.usecases.userdata.GetUserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumScreenViewModel @Inject constructor(
    private val getAlbum: GetAlbum,
    private val getAlbumTracks: GetAlbumTracks,
    getUserData: GetUserData,
    private val flipUserDataScheduled: FlipUserDataScheduled,
    private val flipUserDataLibrary: FlipUserDataLibrary,
) : ViewModel() {

    private val albumId = MutableLiveData<String>()

    val state: LiveData<AlbumScreenState> = albumId.distinctUntilChanged().switchMap { albumId ->
        liveData(Dispatchers.Default) {

            emit(AlbumScreenState.Loading)

            combine(
                getAlbum(albumId),
                getAlbumTracks(albumId),
                getUserData(albumId, SpotifyType.ALBUM)
            ) { albumResource, tracksResource, userData ->

                when (albumResource) {
                    is NetworkResource.Error -> emit(AlbumScreenState.AlbumNotFound)
                    is NetworkResource.Loading -> emit(AlbumScreenState.Loading)
                    is NetworkResource.Ready -> {

                        val tracks = when (tracksResource) {
                            is NetworkResource.Ready -> tracksResource.value.sortedBy { it.trackNumber }
                            else -> emptyList()
                        }

                        emit(
                            AlbumScreenState.Ready(
                                album = albumResource.value,
                                tracks = tracks,
                                userData = userData
                            )
                        )
                    }
                }
            }.collect()
        }
    }

    fun setAlbumId(id: String) {
        albumId.value = id
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
