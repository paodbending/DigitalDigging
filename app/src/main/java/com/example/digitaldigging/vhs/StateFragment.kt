package com.example.digitaldigging.vhs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.launch

/**
 * Base class for a Fragment that relies on a StateHolder for retrieving its State.
 */
abstract class StateFragment<TStateHolder : ViewModelStateHolder<TState>, TState : Any, TBinding : ViewBinding> :
    Fragment() {

    // Since we're using generics we can't get the ViewModel here and we have to rely on child class
    protected abstract val viewModel: TStateHolder

    // Since we're using generics, we can't generate the ViewBindings here and we have to rely on child class
    protected abstract fun generateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): TBinding

    /**
     * Called once during onCreateView.
     * Allows the Fragment to set up things like onClickListeners and Adapters.
     */
    protected abstract fun setUpBindings()

    /**
     * Called every time the State provided by the StateHolder changes.
     * Allows the Fragment to update its appearance accordingly to the State.
     */
    protected abstract fun onStateChanged(state: TState)

    // Internal logic
    private var _binding: TBinding? = null
    protected val binding: TBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = generateBinding(inflater, container)

        // Set up bindings
        setUpBindings()

        // Observe the state (emulates a LiveData)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateFlow.collect { onStateChanged(it) }
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    /**
     * Retrieves the current state.
     */
    protected fun getCurrentState(): TState = viewModel.state

    /**
     * Executes the provided [block] using the current State.
     */
    protected inline fun withState(block: (TState) -> Unit) = block(getCurrentState())
}