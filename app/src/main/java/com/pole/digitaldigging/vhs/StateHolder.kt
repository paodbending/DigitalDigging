package com.pole.digitaldigging.vhs

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * Contains a State and collects a "State Generator" Flow for updating its value.
 */
@OptIn(ExperimentalCoroutinesApi::class)
abstract class StateHolder<TState : Any> : State<TState> {

    // Coroutine Scope
    protected val longLivedScope: CoroutineScope = MainScope()

    // The State should be able to reference the StateHolder that created it, so we can't pass it
    abstract fun buildInitialState(): TState

    // Current State stored inside this StateHolder.
    private val mutableState: MutableState<TState> by lazy {
        mutableStateOf(
            buildInitialState()
        )
    }

    // Delegate the inheritance of State<TState> to the contained MutableState
    override val value: TState
        get() = mutableState.value

    // Flow of "State Generator" Flows.
    private val stateGeneratorsFlow: MutableStateFlow<Flow<TState>> = MutableStateFlow(emptyFlow())

    init {
        // Starts a coroutine for collecting States from the latest "State Generator" Flow.
        longLivedScope.launch(Dispatchers.Default) {
            // We are using a Flow of generators so we can use "flatMapLatest" to stop
            // collecting the old generator and start collecting the new one.
            stateGeneratorsFlow.flatMapLatest { it }.collect { state ->
                // Update the current State
                withContext(Dispatchers.Main) {
                    mutableState.value = state
                }
            }
        }
    }

    protected inline fun setStateGenerator(crossinline block: suspend FlowCollector<TState>.() -> Unit) {
        setStateGenerator(flow { block() }.flowOn(Dispatchers.Default))
    }

    protected fun setStateGenerator(flow: Flow<TState>) {
        stateGeneratorsFlow.value = flow
    }

    protected fun setState(newState: TState) {
        setStateGenerator(flowOf(newState))
    }
}