package com.pole.digitaldigging

import androidx.compose.runtime.State
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.pole.digitaldigging.state.main.MainState
import com.pole.digitaldigging.state.main.MainStateStack
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    mainStateStack: MainStateStack,
) : ViewModel() {

    val mainState: State<MainState> = mainStateStack

    val ready: LiveData<Boolean> = liveData(Dispatchers.Default) {

        delay(500)

        emit(true)
    }
}