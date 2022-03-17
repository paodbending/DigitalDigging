package com.example.digitaldigging.screens.artistinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pole.domain.usecases.GetArtistAlbums
import com.pole.domain.usecases.GetArtistInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistInfoViewModel @Inject constructor(
    private val getArtistInfo: GetArtistInfo,
    private val getArtistAlbums: GetArtistAlbums,
) : ViewModel() {

    private val _state = MutableLiveData<ViewState>(Loading)
    val state: LiveData<ViewState> = _state

    fun setSpotifyId(spotifyId: String) {
        viewModelScope.launch {
            val artistAsync = async { getArtistInfo(spotifyId) }
            val albumsAsync = async { getArtistAlbums(spotifyId) }

            val artist = artistAsync.await()

            if (artist == null) {
                _state.postValue(ArtistNotFound)
            } else {
                val albums = albumsAsync.await()

                _state.postValue(
                    Ready(
                        artist,
                        albums
                    )
                )
            }
        }
    }
}