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
import com.example.digitaldigging.UIResource
import com.example.digitaldigging.databinding.FragmentAlbumScreenBinding
import com.example.digitaldigging.screens.common.artistlist.ArtistsAdapter
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
        savedInstanceState: Bundle?,
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

        val artistsAdapter = ArtistsAdapter { artist ->
            findNavController().navigate(
                AlbumScreenFragmentDirections.actionAlbumScreenFragmentToArtistScreenFragment(
                    artist.id
                )
            )
        }

        binding.tracksRecyclerView.adapter = trackAdapter
        binding.artistsRecyclerView.adapter = artistsAdapter

        viewModel.state.observe(viewLifecycleOwner) { state ->

            binding.progressCircular.visibility =
                if (state is AlbumScreenState.Loading) View.VISIBLE else View.GONE

            binding.networkErrorLayout.root.visibility =
                if (state is AlbumScreenState.Error) View.VISIBLE else View.GONE

            binding.readyLayout.visibility =
                if (state is AlbumScreenState.Ready) View.VISIBLE else View.GONE

            if (state is AlbumScreenState.Ready) {
                binding.albumNameTextView.text = state.album.name


                binding.albumYearTextView.text = state.album.releaseDate.year.toString()
                Glide
                    .with(this)
                    .load(state.album.imageUrl)
                    .centerCrop()
                    .into(binding.albumImageView)


                binding.artistsProgressIndicator.visibility =
                    if (state.artists is UIResource.Loading) View.VISIBLE else View.GONE
                binding.artistsError.root.visibility =
                    if (state.artists is UIResource.Error) View.VISIBLE else View.GONE
                binding.artistsRecyclerView.visibility =
                    if (state.artists is UIResource.Ready) View.VISIBLE else View.GONE

                artistsAdapter.submitList(if (state.artists is UIResource.Ready) state.artists.value else emptyList())


                binding.tracksProgressCircular.visibility =
                    if (state.tracks is UIResource.Loading) View.VISIBLE else View.GONE
                binding.tracksErrorLayout.root.visibility =
                    if (state.tracks is UIResource.Error) View.VISIBLE else View.GONE
                binding.tracksRecyclerView.visibility =
                    if (state.tracks is UIResource.Ready) View.VISIBLE else View.GONE

                trackAdapter.submitList(if (state.tracks is UIResource.Ready) state.tracks.value else emptyList())

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

        viewModel.setAlbumId(navArgs.spotifyId)

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}