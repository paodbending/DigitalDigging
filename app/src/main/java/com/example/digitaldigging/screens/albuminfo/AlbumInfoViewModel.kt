package com.example.digitaldigging.screens.albuminfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pole.domain.usecases.spotify.GetAlbumInfo
import com.pole.domain.usecases.spotify.GetAlbumTracks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumInfoViewModel @Inject constructor(
    private val getAlbumInfo: GetAlbumInfo,
    private val getAlbumTracks: GetAlbumTracks
) : ViewModel() {

    private val _state = MutableLiveData<AlbumInfoViewState>(Loading)
    val state: LiveData<AlbumInfoViewState> = _state

    fun setSpotifyId(spotifyId: String) {
        viewModelScope.launch {
            val albumInfoAsync = async { getAlbumInfo(spotifyId) }
            val albumTracksAsync = async { getAlbumTracks(spotifyId) }

            val album = albumInfoAsync.await()

            if (album != null) {
                _state.postValue(
                    Ready(
                        album,
                        albumTracksAsync.await()
                    )
                )
            } else {
                _state.postValue(AlbumNotFound)
            }
        }
    }
}
