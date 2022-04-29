package com.example.digitaldigging.vhs

import androidx.compose.runtime.*

/**
 * Helper function for observing the State provided from a StateHolder inside a Composable
 */
@Composable
fun <TStateHolder : ViewModelStateHolder<TState>, TState : Any> rememberStateFrom(stateHolder: TStateHolder): State<TState> {
    // We need to transform the StateFlow to a MutableState.
    val mutableState = remember(stateHolder) {
        mutableStateOf(stateHolder.state)
    }
    // The State will be collected as long as this Composable stays inside the composition.
    LaunchedEffect(stateHolder) {
        stateHolder.stateFlow.collect { mutableState.value = it }
    }
    return mutableState
}
