package com.example.digitaldigging.screens.artistinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pole.data.RepositoryImpl
import com.pole.domain.Repository
import com.pole.domain.model.ArtistInfo
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ArtistInfoViewModel : ViewModel() {

    private lateinit var repo: Repository

    private val _artistInfo = MutableLiveData<ArtistInfo>()
    val artistInfo: LiveData<ArtistInfo> = _artistInfo

    private val suspendInit: Job = viewModelScope.launch {
        repo = RepositoryImpl.create()
    }

    fun setSpotifyId(spotifyId: String) {
        viewModelScope.launch {
            suspendInit.join()
            _artistInfo.postValue(repo.getArtist(spotifyId))
        }
    }
}