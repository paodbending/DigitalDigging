package com.example.digitaldigging.screens.searchscreen.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.digitaldigging.databinding.FragmentSearchScreenBinding
import com.example.digitaldigging.screens.searchscreen.state.SearchScreenState
import com.example.digitaldigging.screens.searchscreen.SearchScreenViewModel
import com.example.digitaldigging.screens.searchscreen.state.data.ArtistsData
import com.example.digitaldigging.screens.searchscreen.state.intents.OnArtistClickIntent
import com.example.digitaldigging.screens.searchscreen.state.data.SearchQueryData
import com.example.digitaldigging.vhs.StateFragment
import com.example.digitaldigging.screens.common.artistlist.ArtistsAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchScreenFragment :
    StateFragment<SearchScreenViewModel, SearchScreenState, FragmentSearchScreenBinding>() {

    override val viewModel: SearchScreenViewModel by viewModels()

    override fun generateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentSearchScreenBinding =
        FragmentSearchScreenBinding.inflate(inflater, container, false)

    override fun setUpBindings() {
        binding.artistsRecyclerView.adapter = artistsAdapter
        binding.searchEditText.doOnTextChanged { text, _, _, _ ->
            withState { state ->
                state.setSearchQuery(text?.toString() ?: "")
            }
        }
    }

    override fun onStateChanged(state: SearchScreenState) {
        // Update query in EditText
        if (state is SearchQueryData) {
            if (binding.searchEditText.text.toString() != state.searchQuery)
                binding.searchEditText.setText(state.searchQuery)
        } else {
            if (binding.searchEditText.text.isNullOrEmpty().not())
                binding.searchEditText.setText("")
        }

        // Set progress indicator visibility
        binding.progressIndicator.visibility =
            if (state is SearchScreenState.Loading) View.VISIBLE else View.GONE

        // Set artists in the list adapter
        artistsAdapter.submitList(if (state is ArtistsData) state.artists else emptyList())
    }

    private val artistsAdapter: ArtistsAdapter = ArtistsAdapter { artist ->
        // Just for this example, navigation logic is implemented directly inside the View.
        findNavController().navigate(
            SearchScreenFragmentDirections.actionSearchScreenFragmentToArtistScreenFragment(
                artist.id
            )
        )

        // This is how it should looks like.
        withState { state ->
            if (state is OnArtistClickIntent) state.onArtistClick(artist.id)
        }
    }
}
