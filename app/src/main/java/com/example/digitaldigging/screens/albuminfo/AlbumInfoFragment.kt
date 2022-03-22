package com.example.digitaldigging.screens.albuminfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.digitaldigging.databinding.FragmentAlbumInfoBinding
import com.example.digitaldigging.screens.common.tracklist.TrackAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlbumInfoFragment : Fragment() {

    private var _binding: FragmentAlbumInfoBinding? = null
    private val binding: FragmentAlbumInfoBinding get() = _binding!!

    private val navArgs: AlbumInfoFragmentArgs by navArgs()
    private val viewModel: AlbumInfoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlbumInfoBinding.inflate(LayoutInflater.from(context), container, false)

        val trackAdapter = TrackAdapter {
            findNavController().navigate(
                AlbumInfoFragmentDirections.actionAlbumInfoFragmentToTrackInfoFragment(
                    it.spotifyId
                )
            )
        }

        binding.tracksRecyclerView.adapter = trackAdapter

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                AlbumNotFound -> {}
                Loading -> {}
                is Ready -> {
                    binding.albumNameTextView.text = state.albumInfo.album.name
                    Glide
                        .with(this)
                        .load(state.albumInfo.album.image?.url)
                        .centerCrop()
                        .into(binding.albumImageView)

                    trackAdapter.submitList(state.tracks)
                }
            }
        }

        viewModel.setSpotifyId(navArgs.spotifyId)

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}