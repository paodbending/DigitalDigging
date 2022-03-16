package com.example.digitaldigging

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pole.data.RepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _ready = MutableLiveData(false)
    val ready: LiveData<Boolean> = _ready

    init {
        viewModelScope.launch(Dispatchers.Default) {
            RepositoryImpl.create()
            _ready.postValue(true)
        }
    }
}