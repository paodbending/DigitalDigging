package com.pole.digitaldigging.vhs

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
abstract class StateStack<TState : Any> : State<TState> {

    // Sometimes the State needs to reference the StateHolder, so we can't pass it
    abstract fun getInitialState(): TState

    // Stack
    private val stack: MutableList<TState> = mutableListOf()

    // Current State stored inside this StateHolder.
    private val mutableState: MutableState<TState> by lazy {
        val state = getInitialState()
        stack.add(0, state)
        mutableStateOf(state)
    }

    // Delegate the inheritance of State<TState> to the contained mutable state
    override val value: TState
        get() = mutableState.value

    protected fun navigateTo(state: TState) {
        stack.add(0, state)
        mutableState.value = state
    }

//    protected fun navigateToSingle(state: TState) {
//        stack.removeAll { state.javaClass == it.javaClass }
//        navigateTo(state)
//    }

    protected fun popBackStack() {
        if (stack.size > 1) {
            stack.removeAt(0)
            mutableState.value = stack.first()
        }
    }
}