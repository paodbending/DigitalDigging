package com.example.digitaldigging.screens.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.digitaldigging.databinding.FragmentSearchBinding
import com.example.digitaldigging.screens.common.artistinfolist.ArtistsInfoAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModels()

    private var _binding: FragmentSearchBinding? = null
    private val binding: FragmentSearchBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSearchBinding.inflate(LayoutInflater.from(context), container, false)

        val artistAdapter = ArtistsInfoAdapter {
            findNavController().navigate(
                SearchFragmentDirections.actionSearchFragmentToArtistInfoFragment(
                    it.artist.id
                )
            )
        }

        binding.artistsRecyclerView.adapter = artistAdapter

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Loading -> {
                    binding.progressIndicator.visibility = View.VISIBLE
                    artistAdapter.submitList(emptyList())
                }
                is SearchResults -> {
                    binding.progressIndicator.visibility = View.GONE
                    artistAdapter.submitList(state.artists)
                }
                Idle -> {
                    binding.progressIndicator.visibility = View.GONE
                    artistAdapter.submitList(emptyList())
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
