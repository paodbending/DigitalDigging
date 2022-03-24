package com.example.digitaldigging.screens.trackinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.digitaldigging.databinding.FragmentTrackInfoBinding
import com.example.digitaldigging.screens.common.artistinfolist.ArtistsInfoAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackInfoFragment : Fragment() {

    private var _binding: FragmentTrackInfoBinding? = null
    private val binding: FragmentTrackInfoBinding get() = _binding!!

    private val navArgs: TrackInfoFragmentArgs by navArgs()
    private val viewModel: TrackInfoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrackInfoBinding.inflate(layoutInflater, container, false)

        viewModel.setSpotifyId(navArgs.spotifyId)

        binding.albumImageView.setOnClickListener {
            viewModel.trackInfo.value?.album?.spotifyId?.let { spotifyId ->
                findNavController().navigate(
                    TrackInfoFragmentDirections.actionTrackInfoFragmentToAlbumInfoFragment(
                        spotifyId
                    )
                )
            }
        }

        val artistAdapter = ArtistsInfoAdapter {
            findNavController().navigate(
                TrackInfoFragmentDirections.actionTrackInfoFragmentToArtistInfoFragment(
                    it.artist.spotifyId
                )
            )
        }
        binding.artistsRecyclerView.adapter = artistAdapter

        viewModel.trackInfo.observe(viewLifecycleOwner) { trackInfo ->
            if (trackInfo != null) {
                binding.trackNameTextView.text = trackInfo.track.name
                binding.durationTextView.text = trackInfo.track.duration
            }
        }

        viewModel.artists.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.progressIndicator.visibility = View.GONE
                artistAdapter.submitList(it)
                binding.artistsRecyclerView.visibility = View.VISIBLE
            } else {
                binding.artistsRecyclerView.visibility = View.GONE
                artistAdapter.submitList(emptyList())
                binding.progressIndicator.visibility = View.VISIBLE
            }
        }

        viewModel.albumImage.observe(viewLifecycleOwner) { imageUrl ->
            Glide.with(this).load(imageUrl).centerInside().into(binding.albumImageView)
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}