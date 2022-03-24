package com.example.digitaldigging.screens.artistinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pole.domain.model.AlbumType
import com.pole.domain.usecases.spotify.GetArtistAlbums
import com.pole.domain.usecases.spotify.GetArtistInfo
import com.pole.domain.usecases.userdata.UserDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistInfoViewModel @Inject constructor(
    private val getArtistInfo: GetArtistInfo,
    private val getArtistAlbums: GetArtistAlbums,
    private val userDataUseCase: UserDataUseCase,
) : ViewModel() {

    private val _state = MutableLiveData<ArtistInfoViewState>(Loading)
    val state: LiveData<ArtistInfoViewState> = _state

    fun setSpotifyId(id: String) {

        viewModelScope.launch {

            val artistAsync = async { getArtistInfo(id) }
            val albumsAsync = async { getArtistAlbums(id) }

            val artistInfo = artistAsync.await()

            if (artistInfo == null) {
                _state.postValue(ArtistNotFound)
            } else {

                val albums = albumsAsync.await()

                _state.postValue(
                    Ready(
                        artistInfo = artistInfo,
                        userData = userDataUseCase.getUserData(artistInfo.artist),
                        albums = albums.filter { it.albumType == AlbumType.ALBUM },
                        singles = albums.filter { it.albumType == AlbumType.SINGLE },
                        appearsOn = albums.filter { it.albumType == AlbumType.APPEARS_ON },
                        compilations = albums.filter { it.albumType == AlbumType.COMPILATION }
                    )
                )
            }
        }
    }

    fun flipLibrary() {
        val currentState = state.value
        if (currentState is Ready) {
            viewModelScope.launch {
                userDataUseCase.setInLibrary(
                    currentState.artistInfo,
                    currentState.userData.library.not()
                )
                _state.postValue(
                    currentState.copy(
                        userData = userDataUseCase.getUserData(currentState.artistInfo)
                    )
                )
            }
        }
    }

    fun flipSchedule() {
        val currentState = state.value
        if (currentState is Ready) {
            viewModelScope.launch {
                userDataUseCase.setScheduled(
                    currentState.artistInfo,
                    currentState.userData.scheduled.not()
                )
                _state.postValue(
                    currentState.copy(
                        userData = userDataUseCase.getUserData(currentState.artistInfo)
                    )
                )
            }
        }
    }
}