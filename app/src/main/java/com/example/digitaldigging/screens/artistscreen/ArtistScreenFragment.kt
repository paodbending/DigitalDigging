package com.example.digitaldigging.screens.artistscreen

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
import com.example.digitaldigging.databinding.FragmentArtistScreenBinding
import com.example.digitaldigging.screens.common.albumlist.AlbumAdapter
import com.pole.domain.model.NetworkResource
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class ArtistScreenFragment : Fragment() {

    private val viewModel: ArtistScreenViewModel by viewModels()
    private val args: ArtistScreenFragmentArgs by navArgs()

    private var _binding: FragmentArtistScreenBinding? = null
    private val binding: FragmentArtistScreenBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentArtistScreenBinding.inflate(LayoutInflater.from(context), container, false)

        viewModel.setArtistId(args.spotifyId)

        binding.addToLibraryButton.setOnClickListener {
            viewModel.flipLibrary()
        }

        binding.scheduleButton.setOnClickListener {
            viewModel.flipSchedule()
        }

        val albumsAdapter = AlbumAdapter { navigateToAlbumInfo(it.id) }
        binding.albumsRecyclerView.adapter = albumsAdapter

        val singlesAdapter = AlbumAdapter { navigateToAlbumInfo(it.id) }
        binding.singlesRecyclerView.adapter = singlesAdapter

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                ArtistScreenState.ArtistNotFound -> {}
                ArtistScreenState.Loading -> {}
                is ArtistScreenState.Ready -> {
                    binding.artistNameTextView.text = state.artist.name
                    binding.followersCountTextView.text = getFollowerString(state.artist.followers)

                    binding.addToLibraryButton.setImageResource(
                        if (state.userData.library) R.drawable.ic_baseline_bookmark_24
                        else R.drawable.ic_baseline_bookmark_border_24
                    )

                    binding.scheduleButton.setImageResource(
                        if (state.userData.scheduled) R.drawable.ic_baseline_watch_later_24
                        else R.drawable.ic_baseline_schedule_24
                    )

                    Glide
                        .with(binding.root)
                        .load(state.artist.imageUrl)
                        .centerCrop()
                        .into(binding.artistImageView)

                    if (state.albums is NetworkResource.Ready) {
                        val artistsAlbum = state.albums.value
                        albumsAdapter.submitList(artistsAlbum.albums)
                        singlesAdapter.submitList(artistsAlbum.singles)
                    }
                }
            }
        }
        return binding.root
    }

    private fun navigateToAlbumInfo(albumId: String) {
        findNavController().navigate(
            ArtistScreenFragmentDirections.actionArtistScreenFragmentToAlbumScreenFragment(
                albumId
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