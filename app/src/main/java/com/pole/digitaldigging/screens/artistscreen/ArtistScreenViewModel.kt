package com.pole.digitaldigging.screens.artistscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.pole.digitaldigging.UIResource
import com.pole.domain.model.NetworkResource
import com.pole.domain.model.spotify.AlbumType
import com.pole.domain.usecases.GetArtist
import com.pole.domain.usecases.GetArtistAlbums
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class ArtistScreenViewModel @Inject constructor(
    private val getArtist: GetArtist,
    private val getArtistAlbums: GetArtistAlbums,
) : ViewModel() {

    private val artistIdFlow = MutableStateFlow("")

    val state: LiveData<ArtistScreenState> = liveData(Dispatchers.Default) {

        emit(ArtistScreenState.Loading)

        artistIdFlow.collectLatest { artistId ->
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
            }.collect { emit(it) }
        }
    }

    fun setArtistId(id: String) {
        artistIdFlow.value = id
    }
}