package com.example.digitaldigging.screens.albumscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.digitaldigging.R
import com.example.digitaldigging.databinding.FragmentAlbumScreenBinding
import com.example.digitaldigging.screens.common.tracklist.TrackAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlbumScreenFragment : Fragment() {

    private var _binding: FragmentAlbumScreenBinding? = null
    private val binding: FragmentAlbumScreenBinding get() = _binding!!

    private val navArgs: AlbumScreenFragmentArgs by navArgs()
    private val viewModel: AlbumScreenViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentAlbumScreenBinding.inflate(LayoutInflater.from(context), container, false)

        binding.addToLibraryButton.setOnClickListener {
            viewModel.flipLibrary()
        }

        binding.scheduleButton.setOnClickListener {
            viewModel.flipSchedule()
        }

        val trackAdapter = TrackAdapter(true) { track ->
            findNavController().navigate(
                AlbumScreenFragmentDirections.actionAlbumScreenFragmentToTrackScreenFragment(
                    track.id
                )
            )
        }

        binding.tracksRecyclerView.adapter = trackAdapter

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                AlbumScreenState.AlbumNotFound -> {}
                AlbumScreenState.Loading -> {}
                is AlbumScreenState.Ready -> {
                    binding.albumNameTextView.text = state.album.name
                    Glide
                        .with(this)
                        .load(state.album.imageUrl)
                        .centerCrop()
                        .into(binding.albumImageView)

                    trackAdapter.submitList(state.tracks)

                    binding.addToLibraryButton.setImageResource(
                        if (state.userData.library) R.drawable.ic_baseline_bookmark_24
                        else R.drawable.ic_baseline_bookmark_border_24
                    )

                    binding.scheduleButton.setImageResource(
                        if (state.userData.scheduled) R.drawable.ic_baseline_watch_later_24
                        else R.drawable.ic_baseline_schedule_24
                    )
                }
            }
        }

        viewModel.setAlbumId(navArgs.spotifyId)

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}