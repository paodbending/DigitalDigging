package com.example.digitaldigging

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pole.domain.Repository
import com.pole.domain.model.SpotifyApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    spotifyApi: SpotifyApi
) : ViewModel() {

    private val _ready = MutableLiveData(false)
    val ready: LiveData<Boolean> = _ready

    init {
        viewModelScope.launch(Dispatchers.Default) {

            // Setup spotify api
            spotifyApi.setup()

            _ready.postValue(true)
        }
    }
}