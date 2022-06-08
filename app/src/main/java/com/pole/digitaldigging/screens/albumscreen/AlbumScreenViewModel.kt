package com.pole.digitaldigging.screens.albumscreen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.pole.digitaldigging.UIResource
import com.pole.digitaldigging.toUIResource
import com.pole.domain.entities.NetworkResource
import com.pole.domain.usecases.GetAlbum
import com.pole.domain.usecases.GetAlbumTracks
import com.pole.domain.usecases.GetArtists
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class AlbumScreenViewModel @Inject constructor(
    private val getAlbum: GetAlbum,
    private val getAlbumTracks: GetAlbumTracks,
    private val getArtists: GetArtists,
) : ViewModel() {

    private val mutableState: MutableState<AlbumScreenState> =
        mutableStateOf(AlbumScreenState.Loading)
    val state: State<AlbumScreenState> = mutableState

    suspend fun collectState(albumId: String) {
        getAlbum(albumId).collectLatest { albumResource ->
            getAlbumTracks(albumId).collectLatest { tracksResource ->
                when (albumResource) {
                    is NetworkResource.Error -> mutableState.value = AlbumScreenState.Error
                    is NetworkResource.Loading -> mutableState.value = AlbumScreenState.Loading
                    is NetworkResource.Ready -> {
                        getArtists(albumResource.value.artistIds.toSet()).collectLatest { artistsResource ->
                            mutableState.value = AlbumScreenState.Ready(
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
                            )
                        }
                    }
                }
            }
        }
    }
}
