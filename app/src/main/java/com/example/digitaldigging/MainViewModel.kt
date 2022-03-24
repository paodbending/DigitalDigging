package com.example.digitaldigging

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pole.domain.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    repository: Repository
) : ViewModel() {

    private val _ready = MutableLiveData(false)
    val ready: LiveData<Boolean> = _ready

    init {
        viewModelScope.launch(Dispatchers.Default) {

            // Setup repository
            repository.setup()

            _ready.postValue(true)
        }
    }
}