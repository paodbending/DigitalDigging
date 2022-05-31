package com.pole.digitaldigging.screens.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.pole.digitaldigging.UIResource
import com.pole.digitaldigging.databinding.FragmentSearchScreenBinding
import com.pole.digitaldigging.screens.common.albumlist.AlbumAdapter
import com.pole.digitaldigging.screens.common.artistlist.ArtistViewHolder
import com.pole.digitaldigging.screens.common.artistlist.ArtistsAdapter
import com.pole.digitaldigging.screens.common.tracklist.TrackAdapter
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

        viewModel.results.observe(viewLifecycleOwner) { results ->

            binding.progressIndicator.setVisibleIf(results is UIResource.Loading)

            binding.errorLayout.setVisibleIf(results is UIResource.Error)

            updateRecyclerViewsVisibility(
                viewModel.searchQuery.value,
                viewModel.searchSettings.value,
                results
            )

            bestsArtistsViewHolder.artist =
                if (results is UIResource.Ready) results.value.artists.firstOrNull() else null
            bestResultsAlbumAdapter.submitList(if (results is UIResource.Ready) results.value.albums else emptyList())
            bestResultsTrackAdapter.submitList(if (results is UIResource.Ready) results.value.tracks else emptyList())

            artistAdapter.submitList(if (results is UIResource.Ready) results.value.artists else emptyList())
            albumAdapter.submitList(if (results is UIResource.Ready) results.value.albums else emptyList())
            trackAdapter.submitList(if (results is UIResource.Ready) results.value.tracks else emptyList())
        }

        viewModel.searchQuery.observe(viewLifecycleOwner) { searchQuery ->
            binding.messageLayout.setVisibleIf(searchQuery.isEmpty())
            if (binding.searchEditText.text.toString() != searchQuery) {
                binding.searchEditText.setText(searchQuery)
            }
        }

        viewModel.searchSettings.observe(viewLifecycleOwner) { searchSettings ->

            binding.searchTypeBestsResults.isSelected =
                searchSettings.searchType == SearchType.ALL
            binding.searchTypeArtists.isSelected =
                searchSettings.searchType == SearchType.ARTISTS
            binding.searchTypeAlbums.isSelected =
                searchSettings.searchType == SearchType.ALBUMS
            binding.searchTypeTracks.isSelected =
                searchSettings.searchType == SearchType.TRACKS

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

            updateRecyclerViewsVisibility(
                viewModel.searchQuery.value,
                searchSettings,
                viewModel.results.value
            )
        }

        return binding.root
    }

    private fun updateRecyclerViewsVisibility(
        searchQuery: String?,
        searchSettings: SearchSettings?,
        results: UIResource<Results>?,
    ) {
        if (searchQuery == null || searchSettings == null || results == null) return

        binding.bestsResults.setVisibleIf(searchQuery.isNotEmpty() && searchSettings.searchType == SearchType.ALL && results is UIResource.Ready)
        binding.artistsRecyclerView.setVisibleIf(searchQuery.isNotEmpty() && results is UIResource.Ready && searchSettings.searchType == SearchType.ARTISTS)
        binding.albumsRecyclerView.setVisibleIf(searchQuery.isNotEmpty() && results is UIResource.Ready && searchSettings.searchType == SearchType.ALBUMS)
        binding.tracksRecyclerView.setVisibleIf(searchQuery.isNotEmpty() && results is UIResource.Ready && searchSettings.searchType == SearchType.TRACKS)
    }

    private fun View.setVisibleIf(bool: Boolean) {
        visibility = if (bool) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
