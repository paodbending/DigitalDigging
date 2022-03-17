package com.example.digitaldigging

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.digitaldigging.inject.AppModule
import com.pole.data.DataModule
import com.pole.domain.DomainModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _ready = MutableLiveData(false)
    val ready: LiveData<Boolean> = _ready

    init {
        viewModelScope.launch(Dispatchers.Default) {
            // Setup dependencies modules
            DomainModule.setup()
            DataModule.setup()
            AppModule.setup()

            _ready.postValue(true)
        }
    }
}