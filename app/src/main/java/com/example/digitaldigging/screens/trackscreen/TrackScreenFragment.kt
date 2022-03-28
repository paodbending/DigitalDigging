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
import com.example.digitaldigging.databinding.FragmentTrackScreenBinding
import com.example.digitaldigging.screens.common.artistinfolist.ArtistsAdapter
import com.example.digitaldigging.screens.common.tracklist.TrackAdapter
import com.pole.domain.model.NetworkResource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackScreenFragment : Fragment() {

    private var _binding: FragmentTrackScreenBinding? = null
    private val binding: FragmentTrackScreenBinding get() = _binding!!

    private val navArgs: TrackScreenFragmentArgs by navArgs()
    private val viewModel: TrackScreenViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrackScreenBinding.inflate(layoutInflater, container, false)

        viewModel.setTrackId(navArgs.spotifyId)

        binding.albumImageView.setOnClickListener {
            val state = viewModel.state.value
            if (state is TrackScreenState.Ready && state.album is NetworkResource.Ready) {
                findNavController().navigate(
                    TrackScreenFragmentDirections.actionTrackScreenFragmentToAlbumScreenFragment(
                        state.album.value.id
                    )
                )
            }
        }

        val artistAdapter = ArtistsAdapter { artist ->
            findNavController().navigate(
                TrackScreenFragmentDirections.actionTrackScreenFragmentToArtistScreenFragment(
                    artist.id
                )
            )
        }
        binding.artistsRecyclerView.adapter = artistAdapter

        val recommendedTracksAdapter = TrackAdapter { track ->
            findNavController().navigate(
                TrackScreenFragmentDirections.actionTrackScreenFragmentSelf(
                    track.id
                )
            )
        }
        binding.recommendedTracksRecyclerView.adapter = recommendedTracksAdapter

        binding.addToLibraryButton.setOnClickListener {
            viewModel.flipLibrary()
        }

        binding.scheduleButton.setOnClickListener {
            viewModel.flipSchedule()
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                TrackScreenState.Loading -> {
                    //todo
                }
                TrackScreenState.TrackNotFound -> {
                    //todo
                }
                is TrackScreenState.Ready -> {

                    binding.trackNameTextView.text = state.track.name
                    binding.durationTextView.text = state.track.duration
                    binding.explicitImageView.visibility =
                        if (state.track.explicit) View.VISIBLE else View.INVISIBLE


                    binding.addToLibraryButton.setImageResource(
                        if (state.userData.library) R.drawable.ic_baseline_bookmark_24
                        else R.drawable.ic_baseline_bookmark_border_24
                    )

                    binding.scheduleButton.setImageResource(
                        if (state.userData.scheduled) R.drawable.ic_baseline_watch_later_24
                        else R.drawable.ic_baseline_schedule_24
                    )

                    when (val artistsResource = state.artists) {
                        is NetworkResource.Error -> {
                            binding.artistsProgressIndicator.visibility = View.GONE
                            binding.artistsRecyclerView.visibility = View.GONE
                            artistAdapter.submitList(emptyList())
                        }
                        is NetworkResource.Loading -> {
                            binding.artistsRecyclerView.visibility = View.GONE
                            artistAdapter.submitList(emptyList())
                            binding.artistsProgressIndicator.visibility = View.VISIBLE
                        }
                        is NetworkResource.Ready -> {
                            binding.artistsProgressIndicator.visibility = View.GONE
                            artistAdapter.submitList(artistsResource.value)
                            binding.artistsRecyclerView.visibility = View.VISIBLE
                        }
                    }

                    when (val albumResource = state.album) {
                        is NetworkResource.Error -> {

                        }
                        is NetworkResource.Loading -> {

                        }
                        is NetworkResource.Ready -> {
                            Glide.with(this).load(albumResource.value.imageUrl).centerInside()
                                .into(binding.albumImageView)
                        }
                    }

                    when (val suggestedTracksResource = state.suggestedTracks) {
                        is NetworkResource.Error -> {
                            binding.suggestedTracksProgressIndicator.visibility = View.GONE
                            binding.recommendedTracksRecyclerView.visibility = View.GONE
                            recommendedTracksAdapter.submitList(emptyList())
                        }
                        is NetworkResource.Loading -> {
                            binding.recommendedTracksRecyclerView.visibility = View.GONE
                            recommendedTracksAdapter.submitList(emptyList())
                            binding.suggestedTracksProgressIndicator.visibility = View.VISIBLE
                        }
                        is NetworkResource.Ready -> {
                            binding.suggestedTracksProgressIndicator.visibility = View.GONE
                            recommendedTracksAdapter.submitList(suggestedTracksResource.value)
                            binding.recommendedTracksRecyclerView.visibility = View.VISIBLE
                        }
                    }
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