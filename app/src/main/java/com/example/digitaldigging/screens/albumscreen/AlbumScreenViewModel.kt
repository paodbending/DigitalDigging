package com.example.digitaldigging.screens.albumscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.digitaldigging.UIResource
import com.example.digitaldigging.toUIResource
import com.pole.domain.model.NetworkResource
import com.pole.domain.usecases.GetAlbum
import com.pole.domain.usecases.GetAlbumTracks
import com.pole.domain.usecases.GetArtists
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class AlbumScreenViewModel @Inject constructor(
    private val getAlbum: GetAlbum,
    private val getAlbumTracks: GetAlbumTracks,
    private val getArtists: GetArtists,
) : ViewModel() {

    private val albumIdFlow = MutableStateFlow("")

    val state: LiveData<AlbumScreenState> = liveData(Dispatchers.Default) {
        albumIdFlow.collectLatest { albumId ->

            emit(AlbumScreenState.Loading)

            getAlbum(albumId).collectLatest { albumResource ->
                getAlbumTracks(albumId).collectLatest { tracksResource ->
                    when (albumResource) {
                        is NetworkResource.Error -> emit(AlbumScreenState.Error)
                        is NetworkResource.Loading -> emit(AlbumScreenState.Loading)
                        is NetworkResource.Ready -> {
                            getArtists(albumResource.value.artistIds.toSet()).collectLatest { artistsResource ->
                                emit(AlbumScreenState.Ready(
                                    album = albumResource.value,
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

    fun setAlbumId(id: String) {
        albumIdFlow.value = id
    }
}
