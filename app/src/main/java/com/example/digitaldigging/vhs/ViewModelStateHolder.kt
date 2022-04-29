package com.example.digitaldigging.vhs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Contains a State and collects a "State Generator" Flow for updating its value.
 */
@OptIn(ExperimentalCoroutinesApi::class)
abstract class ViewModelStateHolder<TState : Any> : ViewModel() {

    // Sometimes the State needs to reference the StateHolder, so we can't pass it
    abstract fun getInitialState(): TState

    // Current State stored inside this StateHolder.
    private val mutableStateFlow: MutableStateFlow<TState> by lazy {
        MutableStateFlow(
            getInitialState()
        )
    }
    val stateFlow: StateFlow<TState> by lazy { mutableStateFlow }
    val state: TState get() = mutableStateFlow.value

    // Flow of "State Generator" Flows.
    private val stateGeneratorsFlow: MutableStateFlow<Flow<TState>> = MutableStateFlow(emptyFlow())

    init {
        // Starts a coroutine for collecting States from the latest "State Generator" Flow.
        viewModelScope.launch {
            // We are using a Flow of generators so we can use "flatMapLatest" to stop
            // collecting the old generator and start collecting the new one.
            stateGeneratorsFlow.flatMapLatest { it }.collect { state ->
                // Update the current State
                mutableStateFlow.value = state
            }
        }
    }

    // Setter for changing the state generators
    protected fun setStateGenerator(newStateFlow: Flow<TState>) {
        stateGeneratorsFlow.value = newStateFlow
    }

    // Nice little helper function (I'm in love with the inline modifier)
    protected inline fun setStateGenerator(crossinline block: suspend FlowCollector<TState>.() -> Unit) {
        setStateGenerator(flow { block() })
    }
}