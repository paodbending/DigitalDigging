package com.example.digitaldigging.screens.trackinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pole.domain.model.ArtistInfo
import com.pole.domain.model.Image
import com.pole.domain.model.TrackInfo
import com.pole.domain.usecases.GetAlbumInfo
import com.pole.domain.usecases.GetArtistInfo
import com.pole.domain.usecases.GetTrackInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackInfoViewModel @Inject constructor(
    private val getTrackInfo: GetTrackInfo,
    private val getArtistInfo: GetArtistInfo,
    private val getAlbumInfo: GetAlbumInfo
) : ViewModel() {

    private val _trackInfo = MutableLiveData<TrackInfo?>()
    val trackInfo: LiveData<TrackInfo?> = _trackInfo

    private val _artists = MutableLiveData<List<ArtistInfo>>()
    val artists: LiveData<List<ArtistInfo>> = _artists

    private val _albumImage = MutableLiveData<Image?>()
    val albumImage: LiveData<Image?> = _albumImage

    fun setSpotifyId(spotifyId: String) {
        viewModelScope.launch {

            val trackInfo = getTrackInfo(spotifyId)

            _trackInfo.postValue(trackInfo)

            if (trackInfo != null) {
                launch {
                    _artists.postValue(
                        trackInfo.track.artists.mapNotNull { getArtistInfo(it.spotifyId) }
                    )
                }
                launch {
                    _albumImage.postValue(
                        getAlbumInfo(trackInfo.album.spotifyId)?.album?.image
                    )
                }
            }
        }
    }
}