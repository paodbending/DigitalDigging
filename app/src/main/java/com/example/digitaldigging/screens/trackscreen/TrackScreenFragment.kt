package com.example.digitaldigging.screens.trackscreen

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
import com.example.digitaldigging.databinding.FragmentTrackScreenBinding
import com.example.digitaldigging.screens.common.artistlist.ArtistsAdapter
import com.example.digitaldigging.screens.common.tracklist.TrackAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackScreenFragment : Fragment() {

    private var _binding: FragmentTrackScreenBinding? = null
    private val binding: FragmentTrackScreenBinding get() = _binding!!

    private val navArgs: TrackScreenFragmentArgs by navArgs()
    private val viewModel: TrackScreenViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTrackScreenBinding.inflate(layoutInflater, container, false)

        viewModel.setTrackId(navArgs.spotifyId)

        binding.trackImageView.setOnClickListener {
            navigateToAlbumScreen()
        }

        binding.albumNameTextView.setOnClickListener {
            navigateToAlbumScreen()
        }

        val artistAdapter = ArtistsAdapter { artist ->
            findNavController().navigate(
                TrackScreenFragmentDirections.actionTrackScreenFragmentToArtistScreenFragment(
                    artist.id
                )
            )
        }
        binding.artistsRecyclerView.adapter = artistAdapter

        val suggestedTracksAdapter = TrackAdapter { track ->
            findNavController().navigate(
                TrackScreenFragmentDirections.actionTrackScreenFragmentSelf(
                    track.id
                )
            )
        }
        binding.suggestedTracksRecyclerView.adapter = suggestedTracksAdapter

        binding.addToLibraryButton.setOnClickListener {
            viewModel.flipLibrary()
        }

        binding.scheduleButton.setOnClickListener {
            viewModel.flipSchedule()
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->

            binding.progressCircular.visibility =
                if (state is TrackScreenState.Loading) View.VISIBLE else View.GONE

            binding.networkErrorLayout.root.visibility =
                if (state is TrackScreenState.Error) View.VISIBLE else View.GONE

            binding.readyLayout.visibility =
                if (state is TrackScreenState.Ready) View.VISIBLE else View.GONE

            if (state is TrackScreenState.Ready) {

                Glide.with(this).load(state.track.imageUrl).centerInside()
                    .into(binding.trackImageView)

                binding.trackNameTextView.text = state.track.name
                binding.durationTextView.text = state.track.duration

                binding.albumNameTextView.text = state.album.name

                binding.explicitImageView.visibility =
                    if (state.track.explicit) View.VISIBLE else View.GONE

                binding.addToLibraryButton.setImageResource(
                    if (state.userData.library) R.drawable.ic_baseline_bookmark_24
                    else R.drawable.ic_baseline_bookmark_border_24
                )

                binding.scheduleButton.setImageResource(
                    if (state.userData.scheduled) R.drawable.ic_baseline_watch_later_24
                    else R.drawable.ic_baseline_schedule_24
                )

                binding.artistsError.root.visibility =
                    if (state.artists is UIResource.Error) View.VISIBLE else View.GONE
                binding.artistsProgressIndicator.visibility =
                    if (state.artists is UIResource.Loading) View.VISIBLE else View.GONE
                binding.artistsRecyclerView.visibility =
                    if (state.artists is UIResource.Ready) View.VISIBLE else View.GONE
                artistAdapter.submitList(if (state.artists is UIResource.Ready) state.artists.value else emptyList())


                binding.suggestedTracksError.root.visibility =
                    if (state.suggestedTracks is UIResource.Error) View.VISIBLE else View.GONE
                binding.suggestedTracksProgressIndicator.visibility =
                    if (state.suggestedTracks is UIResource.Loading) View.VISIBLE else View.GONE
                binding.suggestedTracksRecyclerView.visibility =
                    if (state.suggestedTracks is UIResource.Ready) View.VISIBLE else View.GONE
                suggestedTracksAdapter.submitList(if (state.suggestedTracks is UIResource.Ready) state.suggestedTracks.value else emptyList())
            }
        }

        return binding.root
    }

    private fun navigateToAlbumScreen() {
        val state = viewModel.state.value
        if (state is TrackScreenState.Ready) {
            findNavController().navigate(
                TrackScreenFragmentDirections.actionTrackScreenFragmentToAlbumScreenFragment(
                    state.album.id
                )
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}