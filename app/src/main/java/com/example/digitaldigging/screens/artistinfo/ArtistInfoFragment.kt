package com.example.digitaldigging.screens.artistinfo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.digitaldigging.R
import com.example.digitaldigging.databinding.FragmentArtistInfoBinding

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

        viewModel.artistInfo.observe(viewLifecycleOwner) {
            binding.artistNameTextView.text = it.artist.name
            binding.followersCountTextView.text = context?.getString(R.string.d_followers, it.followers)

            val firstImage = it.images.firstOrNull()
            if (firstImage != null) {
                Glide
                    .with(binding.root)
                    .load(firstImage.url)
                    .centerCrop()
                    .into(binding.artistImageView)
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}