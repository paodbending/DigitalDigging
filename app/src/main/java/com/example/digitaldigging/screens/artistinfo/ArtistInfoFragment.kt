package com.example.digitaldigging.screens.artistinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.digitaldigging.R
import com.example.digitaldigging.databinding.FragmentArtistInfoBinding
import com.example.digitaldigging.screens.common.albumlist.AlbumAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class ArtistInfoFragment : Fragment() {

    private val viewModel: ArtistInfoViewModel by viewModels()
    private val args: ArtistInfoFragmentArgs by navArgs()

    private var _binding: FragmentArtistInfoBinding? = null
    private val binding: FragmentArtistInfoBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArtistInfoBinding.inflate(LayoutInflater.from(context), container, false)

        viewModel.setSpotifyId(args.spotifyId)

        val albumsAdapter = AlbumAdapter {
            navigateToAlbumInfo(it.spotifyId)
        }
        binding.albumsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = albumsAdapter
        }

        val singlesAdapter = AlbumAdapter {
            navigateToAlbumInfo(it.spotifyId)
        }
        binding.singlesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = singlesAdapter
        }

        viewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                ArtistNotFound -> {}
                Loading -> {}
                is Ready -> {
                    binding.artistNameTextView.text = it.artistInfo.artist.name
                    binding.followersCountTextView.text = getFollowerString(it.artistInfo.followers)

                    Glide
                        .with(binding.root)
                        .load(it.artistInfo.image?.url)
                        .centerCrop()
                        .into(binding.artistImageView)

                    albumsAdapter.submitList(it.albums)
                    singlesAdapter.submitList(it.singles)
                }
            }
        }

        return binding.root
    }

    private fun navigateToAlbumInfo(spotifyId: String) {
        findNavController().navigate(
            ArtistInfoFragmentDirections.actionArtistInfoFragmentToAlbumInfoFragment(
                spotifyId
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun getFollowerString(followers: Int?): String? {
        return if (followers == null) null else context?.getString(
            R.string.s_followers, when {
                followers > 1_000_000_000 -> "${(followers / 1_000_000 * 1000.0).roundToInt() / 1000.0} B"
                followers > 1_000_000 -> "${(followers / 1_000_000 * 1000.0).roundToInt() / 1000.0} M"
                followers > 1_000 -> "${(followers / 1_000 * 1000.0).roundToInt() / 1000.0} K"
                else -> followers.toString()
            }
        )
    }
}