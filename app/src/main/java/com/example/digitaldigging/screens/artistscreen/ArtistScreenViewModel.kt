package com.example.digitaldigging.screens.artistscreen

import androidx.lifecycle.*
import com.pole.domain.model.NetworkResource
import com.pole.domain.model.spotify.SpotifyType
import com.pole.domain.model.spotify.AlbumType
import com.pole.domain.usecases.spotify.GetArtist
import com.pole.domain.usecases.spotify.GetArtistAlbums
import com.pole.domain.usecases.userdata.FlipUserDataLibrary
import com.pole.domain.usecases.userdata.FlipUserDataScheduled
import com.pole.domain.usecases.userdata.GetUserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistScreenViewModel @Inject constructor(
    private val getArtist: GetArtist,
    private val getArtistAlbums: GetArtistAlbums,
    private val flipUserDataLibrary: FlipUserDataLibrary,
    private val flipUserDataScheduled: FlipUserDataScheduled,
    private val getUserData: GetUserData
) : ViewModel() {

    private val artistId = MutableLiveData<String>()

    val state: LiveData<ArtistInfoScreenState> =
        artistId.distinctUntilChanged().switchMap { artistId ->
            liveData(Dispatchers.Default) {

                emit(Loading)

                combine(
                    getArtist(artistId),
                    getArtistAlbums(artistId),
                    getUserData(artistId, SpotifyType.ARTIST)
                ) { artistResource, albumsResource, userData ->
                    when (artistResource) {
                        is NetworkResource.Error -> ArtistNotFound
                        is NetworkResource.Loading -> Loading
                        is NetworkResource.Ready -> {

                            val albums = when (albumsResource) {
                                is NetworkResource.Ready -> albumsResource.value.sortedByDescending { it.popularity }
                                else -> emptyList()
                            }

                            Ready(
                                artist = artistResource.value,
                                userData = userData,
                                albums = albums.filter { it.albumType == AlbumType.ALBUM },
                                singles = albums.filter { it.albumType == AlbumType.SINGLE },
                                appearsOn = albums.filter { it.albumType == AlbumType.APPEARS_ON },
                            )
                        }
                    }

                }.collect { emit(it) }
            }
        }

    fun setArtistId(id: String) {
        artistId.value = id
    }

    fun flipLibrary() {
        val currentState = state.value
        if (currentState is Ready) {
            viewModelScope.launch {
                flipUserDataLibrary(currentState.artist.id, SpotifyType.ARTIST)
            }
        }
    }

    fun flipSchedule() {
        val currentState = state.value
        if (currentState is Ready) {
            viewModelScope.launch {
                flipUserDataScheduled(currentState.artist.id, SpotifyType.ARTIST)
            }
        }
    }
}