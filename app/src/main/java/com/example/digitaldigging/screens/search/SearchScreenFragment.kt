package com.example.digitaldigging.screens.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.digitaldigging.databinding.FragmentSearchScreenBinding
import com.example.digitaldigging.screens.common.artistinfolist.ArtistsAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchScreenFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModels()

    private var _binding: FragmentSearchScreenBinding? = null
    private val binding: FragmentSearchScreenBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentSearchScreenBinding.inflate(LayoutInflater.from(context), container, false)

        val artistAdapter = ArtistsAdapter { artist ->
            findNavController().navigate(
                SearchScreenFragmentDirections.actionSearchScreenFragmentToArtistScreenFragment(
                    artist.id
                )
            )
        }

        binding.artistsRecyclerView.adapter = artistAdapter

        viewModel.screenState.observe(viewLifecycleOwner) { state ->
            when (state) {
                SearchScreenState.NetworkError -> {
                    binding.progressIndicator.visibility = View.GONE
                    artistAdapter.submitList(emptyList())
                }
                SearchScreenState.Idle -> {
                    binding.progressIndicator.visibility = View.GONE
                    artistAdapter.submitList(emptyList())
                }
                is SearchScreenState.Loading -> {
                    binding.progressIndicator.visibility = View.VISIBLE
                    artistAdapter.submitList(emptyList())
                }
                is SearchScreenState.Results -> {
                    binding.progressIndicator.visibility = View.GONE
                    artistAdapter.submitList(state.artists)
                }
            }
        }

        binding.searchEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.search(text?.toString() ?: "")
        }

        return binding.root
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
