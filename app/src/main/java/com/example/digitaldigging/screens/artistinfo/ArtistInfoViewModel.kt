package com.example.digitaldigging.screens.artistinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pole.domain.Repository
import com.pole.domain.model.ArtistInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistInfoViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _artistInfo = MutableLiveData<ArtistInfo>()
    val artistInfo: LiveData<ArtistInfo> = _artistInfo

    fun setSpotifyId(spotifyId: String) {
        viewModelScope.launch {
            _artistInfo.postValue(repository.getArtist(spotifyId))
        }
    }
}