package com.example.digitaldigging

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
//    repository: Repository
) : ViewModel() {

    val ready: LiveData<Boolean> = liveData(Dispatchers.Default) {

        // Setup repository
//            repository.setup()

        delay(500)

        emit(true)
    }
}