package com.example.digitaldigging.screens.artistinfo

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

        val appearsOnAdapter = AlbumAdapter { navigateToAlbumInfo(it.id) }
        binding.appearsRecyclerView.adapter = appearsOnAdapter

        val compilationsAdapter = AlbumAdapter { navigateToAlbumInfo(it.id) }
        binding.compilationRecyclerView.adapter = compilationsAdapter

        viewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                ArtistNotFound -> {}
                Loading -> {}
                is Ready -> {
                    binding.artistNameTextView.text = it.artistInfo.name
                    binding.followersCountTextView.text = getFollowerString(it.artistInfo.followers)

                    binding.addToLibraryButton.setImageResource(
                        if (it.userData.library) R.drawable.ic_baseline_bookmark_24
                        else R.drawable.ic_baseline_bookmark_border_24
                    )

                    binding.scheduleButton.setImageResource(
                        if (it.userData.scheduled) R.drawable.ic_baseline_watch_later_24
                        else R.drawable.ic_baseline_schedule_24
                    )

                    Glide
                        .with(binding.root)
                        .load(it.artistInfo.imageUrl)
                        .centerCrop()
                        .into(binding.artistImageView)

                    albumsAdapter.submitList(it.albums)
                    singlesAdapter.submitList(it.singles)
                    appearsOnAdapter.submitList(it.appearsOn)
                    compilationsAdapter.submitList(it.compilations)
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