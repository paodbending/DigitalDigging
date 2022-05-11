package com.example.digitaldigging.screens.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.digitaldigging.UIResource
import com.example.digitaldigging.databinding.FragmentSearchScreenBinding
import com.example.digitaldigging.screens.common.albumlist.AlbumAdapter
import com.example.digitaldigging.screens.common.artistlist.ArtistViewHolder
import com.example.digitaldigging.screens.common.artistlist.ArtistsAdapter
import com.example.digitaldigging.screens.common.tracklist.TrackAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchScreenFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModels()

    private var _binding: FragmentSearchScreenBinding? = null
    private val binding: FragmentSearchScreenBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding =
            FragmentSearchScreenBinding.inflate(LayoutInflater.from(context), container, false)

        val onBackPressCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                binding.searchEditText.setText("")
            }
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, onBackPressCallback)

        val artistAdapter = ArtistsAdapter { artist ->
            findNavController().navigate(
                SearchScreenFragmentDirections.actionSearchScreenFragmentToArtistScreenFragment(
                    artist.id
                )
            )
        }

        binding.artistsRecyclerView.adapter = artistAdapter


        val bestResultsAlbumAdapter = AlbumAdapter(wrap = false) { album ->
            findNavController().navigate(
                SearchScreenFragmentDirections.actionSearchScreenToAlbumScreenFragment(
                    album.id
                )
            )
        }
        binding.bestResultsAlbumsRecyclerView.adapter = bestResultsAlbumAdapter

        val albumAdapter = AlbumAdapter(wrap = true) { album ->
            findNavController().navigate(
                SearchScreenFragmentDirections.actionSearchScreenToAlbumScreenFragment(
                    album.id
                )
            )
        }
        binding.albumsRecyclerView.adapter = albumAdapter

        val bestResultsTrackAdapter = TrackAdapter(showTrackNumber = false) { track ->
            findNavController().navigate(
                SearchScreenFragmentDirections.actionSearchScreenToTrackScreenFragment(
                    track.id
                )
            )
        }
        binding.bestResultsTracksRecyclerView.adapter = bestResultsTrackAdapter

        val trackAdapter = TrackAdapter(showTrackNumber = false) { track ->
            findNavController().navigate(
                SearchScreenFragmentDirections.actionSearchScreenToTrackScreenFragment(
                    track.id
                )
            )
        }
        binding.tracksRecyclerView.adapter = trackAdapter

        val bestsArtistsViewHolder = ArtistViewHolder(
            binding.bestResultsArtist
        ) { artist ->
            findNavController().navigate(
                SearchScreenFragmentDirections.actionSearchScreenFragmentToArtistScreenFragment(
                    artist.id
                )
            )
        }

        binding.searchEditText.doOnTextChanged { text, _, _, _ ->
            onBackPressCallback.isEnabled = text.isNullOrEmpty().not()
            viewModel.search(text?.toString() ?: "")
        }

        binding.searchTypeBestsResults.setOnClickListener {
            viewModel.setSearchType(SearchType.ALL)
        }
        binding.searchTypeArtists.setOnClickListener {
            viewModel.setSearchType(SearchType.ARTISTS)
        }
        binding.searchTypeAlbums.setOnClickListener {
            viewModel.setSearchType(SearchType.ALBUMS)
        }
        binding.searchTypeTracks.setOnClickListener {
            viewModel.setSearchType(SearchType.TRACKS)
        }


        binding.artistSortRelevance.setOnClickListener {
            viewModel.setArtistSortType(ArtistSortType.RELEVANCE)
        }
        binding.artistSortPopularity.setOnClickListener {
            viewModel.setArtistSortType(ArtistSortType.POPULARITY)
        }
        binding.artistSortFollowers.setOnClickListener {
            viewModel.setArtistSortType(ArtistSortType.FOLLOWERS)
        }

        binding.albumSortRelevance.setOnClickListener {
            viewModel.setAlbumSortType(AlbumSortType.RELEVANCE)
        }
        binding.albumSortPopularity.setOnClickListener {
            viewModel.setAlbumSortType(AlbumSortType.POPULARITY)
        }
        binding.albumSortReleaseDate.setOnClickListener {
            viewModel.setAlbumSortType(AlbumSortType.RELEASE_DATE)
        }

        binding.trackSortRelevance.setOnClickListener {
            viewModel.setTrackSortType(TrackSortType.RELEVANCE)
        }
        binding.trackSortPopularity.setOnClickListener {
            viewModel.setTrackSortType(TrackSortType.POPULARITY)
        }
        binding.trackSortLength.setOnClickListener {
            viewModel.setTrackSortType(TrackSortType.LENGTH)
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->

            state.searchSettings.let { searchSettings ->

                binding.searchTypeBestsResults.isSelected =
                    searchSettings.searchType == SearchType.ALL
                binding.searchTypeArtists.isSelected =
                    searchSettings.searchType == SearchType.ARTISTS
                binding.searchTypeAlbums.isSelected =
                    searchSettings.searchType == SearchType.ALBUMS
                binding.searchTypeTracks.isSelected =
                    searchSettings.searchType == SearchType.TRACKS

                binding.bestsResults.setVisibleIf(searchSettings.searchType == SearchType.ALL && state.results is UIResource.Ready)
                binding.artistsRecyclerView.setVisibleIf(!(state.results !is UIResource.Ready || searchSettings.searchType != SearchType.ARTISTS))
                binding.albumsRecyclerView.setVisibleIf(!(state.results !is UIResource.Ready || searchSettings.searchType != SearchType.ALBUMS))
                binding.tracksRecyclerView.setVisibleIf(!(state.results !is UIResource.Ready || searchSettings.searchType != SearchType.TRACKS))

                binding.artistSortTypeSelectors.setVisibleIf(searchSettings.searchType == SearchType.ARTISTS)
                binding.artistSortRelevance.isSelected =
                    searchSettings.artistSortType == ArtistSortType.RELEVANCE
                binding.artistSortFollowers.isSelected =
                    searchSettings.artistSortType == ArtistSortType.FOLLOWERS
                binding.artistSortPopularity.isSelected =
                    searchSettings.artistSortType == ArtistSortType.POPULARITY

                binding.albumSortTypeSelectors.setVisibleIf(searchSettings.searchType == SearchType.ALBUMS)
                binding.albumSortRelevance.isSelected =
                    searchSettings.albumSortType == AlbumSortType.RELEVANCE
                binding.albumSortPopularity.isSelected =
                    searchSettings.albumSortType == AlbumSortType.POPULARITY
                binding.albumSortReleaseDate.isSelected =
                    searchSettings.albumSortType == AlbumSortType.RELEASE_DATE

                binding.trackSortTypeSelectors.setVisibleIf(searchSettings.searchType == SearchType.TRACKS)
                binding.trackSortRelevance.isSelected =
                    searchSettings.trackSortType == TrackSortType.RELEVANCE
                binding.trackSortPopularity.isSelected =
                    searchSettings.trackSortType == TrackSortType.POPULARITY
                binding.trackSortLength.isSelected =
                    searchSettings.trackSortType == TrackSortType.LENGTH
            }

            binding.progressIndicator.setVisibleIf(state.results is UIResource.Loading && state.searchQuery.isNotEmpty())

            binding.errorLayout.setVisibleIf(state.results is UIResource.Error)

            binding.messageLayout.setVisibleIf(state.searchQuery.isEmpty())

            bestsArtistsViewHolder.artist =
                if (state.results is UIResource.Ready) state.results.value.artists.firstOrNull() else null

            artistAdapter.submitList(if (state.results is UIResource.Ready) state.results.value.artists else emptyList())
            bestResultsAlbumAdapter.submitList(if (state.results is UIResource.Ready) state.results.value.albums.take(
                5) else emptyList())
            albumAdapter.submitList(if (state.results is UIResource.Ready) state.results.value.albums else emptyList())
            trackAdapter.submitList(if (state.results is UIResource.Ready) state.results.value.tracks else emptyList())
            bestResultsTrackAdapter.submitList(if (state.results is UIResource.Ready) state.results.value.tracks.take(
                5) else emptyList())
        }

        return binding.root
    }

    private fun View.setVisibleIf(bool: Boolean) {
        visibility = if (bool) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
