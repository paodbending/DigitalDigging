package com.pole.digitaldigging.screens.artistscreen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.pole.digitaldigging.UIResource
import com.pole.domain.entities.AlbumType
import com.pole.domain.entities.NetworkResource
import com.pole.domain.usecases.GetArtist
import com.pole.domain.usecases.GetArtistAlbums
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class ArtistScreenViewModel @Inject constructor(
    private val getArtist: GetArtist,
    private val getArtistAlbums: GetArtistAlbums,
) : ViewModel() {

    private val mutableState: MutableState<ArtistScreenState> =
        mutableStateOf(ArtistScreenState.Loading)
    val state: State<ArtistScreenState> = mutableState

    suspend fun collectState(artistId: String) {
        combine(
            getArtist(artistId),
            getArtistAlbums(artistId),
        ) { artistResource, albumsResource ->
            when (artistResource) {
                is NetworkResource.Error -> ArtistScreenState.Error
                is NetworkResource.Loading -> ArtistScreenState.Loading
                is NetworkResource.Ready -> ArtistScreenState.Ready(
                    artist = artistResource.value,
                    albums = when (albumsResource) {
                        is NetworkResource.Loading -> UIResource.Loading()
                        is NetworkResource.Error -> UIResource.Error(albumsResource.appError)
                        is NetworkResource.Ready -> UIResource.Ready(ArtistScreenState.Ready.Albums(
                            albums = albumsResource.value.filter { it.albumType == AlbumType.ALBUM },
                            singles = albumsResource.value.filter { it.albumType == AlbumType.SINGLE },
                        ))
                    }
                )
            }
        }.collect { mutableState.value = it }
    }
}