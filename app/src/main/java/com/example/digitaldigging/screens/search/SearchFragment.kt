package com.example.digitaldigging.screens.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.digitaldigging.databinding.FragmentSearchBinding
import com.example.digitaldigging.screens.ui.ArtistsInfoAdapter

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModels()

    private var _binding: FragmentSearchBinding? = null
    private val binding: FragmentSearchBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(LayoutInflater.from(context), container, false)

        val adapter = ArtistsInfoAdapter {
            findNavController().navigate(
                SearchFragmentDirections.actionSearchFragmentToArtistInfoFragment(
                    it.artist.spotifyId
                )
            )
        }

        binding.artistsRecyclerView.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(context)
        }

        binding.searchEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.search(text?.toString() ?: "")
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                Loading -> {
                    binding.progressIndicator.visibility = View.VISIBLE
                    adapter.submitList(emptyList())
                }
                is SearchResults -> {
                    binding.progressIndicator.visibility = View.GONE
                    adapter.submitList(state.artists)
                }
                Idle -> {
                    binding.progressIndicator.visibility = View.GONE
                    adapter.submitList(emptyList())
                }
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
