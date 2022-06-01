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
import com.pole.digitaldigging.databinding.FragmentSearchScreenBinding
import com.pole.digitaldigging.screens.common.albumlist.AlbumAdapter
import com.pole.digitaldigging.screens.common.artistlist.ArtistViewHolder
import com.pole.digitaldigging.screens.common.artistlist.ArtistsAdapter
import com.pole.digitaldigging.screens.common.tracklist.TrackAdapter
import com.pole.domain.entities.Album
import com.pole.domain.entities.Artist
import com.pole.domain.entities.Track
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchScreenFragment : Fragment(), com.pole.digitaldigging.screens.search.View {

    private val viewModel: SearchViewModel by viewModels()

    private var _binding: FragmentSearchScreenBinding? = null
    private val binding: FragmentSearchScreenBinding get() = _binding!!

    private val presenter: Presenter by lazy {
        viewModel.buildPresenter(
            view = this,
            lifecycleOwner = viewLifecycleOwner
        )
    }

    private val bestsArtistsViewHolder by lazy {
        ArtistViewHolder(
            binding.bestResultsArtist
        ) { artist ->
            findNavController().navigate(
                SearchScreenFragmentDirections.actionSearchScreenFragmentToArtistScreenFragment(
                    artist.id
                )
            )
        }
    }

    private val bestResultsAlbumAdapter = AlbumAdapter(wrap = false) { album ->
        findNavController().navigate(
            SearchScreenFragmentDirections.actionSearchScreenToAlbumScreenFragment(
                album.id
            )
        )
    }

    private val bestResultsTrackAdapter = TrackAdapter(showTrackNumber = false) { track ->
        findNavController().navigate(
            SearchScreenFragmentDirections.actionSearchScreenToTrackScreenFragment(
                track.id
            )
        )
    }

    private val artistAdapter = ArtistsAdapter { artist ->
        findNavController().navigate(
            SearchScreenFragmentDirections.actionSearchScreenFragmentToArtistScreenFragment(
                artist.id
            )
        )
    }

    private val albumAdapter = AlbumAdapter(wrap = true) { album ->
        findNavController().navigate(
            SearchScreenFragmentDirections.actionSearchScreenToAlbumScreenFragment(
                album.id
            )
        )
    }

    private val trackAdapter = TrackAdapter(showTrackNumber = false) { track ->
        findNavController().navigate(
            SearchScreenFragmentDirections.actionSearchScreenToTrackScreenFragment(
                track.id
            )
        )
    }

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

        binding.bestResultsAlbumsRecyclerView.adapter = bestResultsAlbumAdapter
        binding.bestResultsTracksRecyclerView.adapter = bestResultsTrackAdapter

        binding.artistsRecyclerView.adapter = artistAdapter
        binding.albumsRecyclerView.adapter = albumAdapter
        binding.tracksRecyclerView.adapter = trackAdapter

        binding.searchEditText.doOnTextChanged { text, _, _, _ ->
            onBackPressCallback.isEnabled = text.isNullOrEmpty().not()
            presenter.setSearchQuery(text?.toString() ?: "")
        }

        binding.searchTypeBestsResults.setOnClickListener {
            presenter.setSearchType(SearchType.ALL)
        }
        binding.searchTypeArtists.setOnClickListener {
            presenter.setSearchType(SearchType.ARTISTS)
        }
        binding.searchTypeAlbums.setOnClickListener {
            presenter.setSearchType(SearchType.ALBUMS)
        }
        binding.searchTypeTracks.setOnClickListener {
            presenter.setSearchType(SearchType.TRACKS)
        }


        binding.artistSortRelevance.setOnClickListener {
            presenter.setArtistSortType(ArtistSortType.RELEVANCE)
        }
        binding.artistSortPopularity.setOnClickListener {
            presenter.setArtistSortType(ArtistSortType.POPULARITY)
        }
        binding.artistSortFollowers.setOnClickListener {
            presenter.setArtistSortType(ArtistSortType.FOLLOWERS)
        }

        binding.albumSortRelevance.setOnClickListener {
            presenter.setAlbumSortType(AlbumSortType.RELEVANCE)
        }
        binding.albumSortPopularity.setOnClickListener {
            presenter.setAlbumSortType(AlbumSortType.POPULARITY)
        }
        binding.albumSortReleaseDate.setOnClickListener {
            presenter.setAlbumSortType(AlbumSortType.RELEASE_DATE)
        }

        binding.trackSortRelevance.setOnClickListener {
            presenter.setTrackSortType(TrackSortType.RELEVANCE)
        }
        binding.trackSortPopularity.setOnClickListener {
            presenter.setTrackSortType(TrackSortType.POPULARITY)
        }
        binding.trackSortLength.setOnClickListener {
            presenter.setTrackSortType(TrackSortType.LENGTH)
        }

        setSearchType(SearchType.ALL)

        return binding.root
    }

    private fun View.setVisibleIf(bool: Boolean) {
        visibility = if (bool) View.VISIBLE else View.GONE
    }

    override fun setSearchQuery(searchQuery: String) {
        if (binding.searchEditText.text.toString() != searchQuery)
            binding.searchEditText.setText(searchQuery)
    }

    override fun setSearchType(searchType: SearchType) {
        binding.searchTypeBestsResults.isSelected = searchType == SearchType.ALL
        binding.searchTypeArtists.isSelected = searchType == SearchType.ARTISTS
        binding.searchTypeAlbums.isSelected = searchType == SearchType.ALBUMS
        binding.searchTypeTracks.isSelected = searchType == SearchType.TRACKS

        binding.artistSortTypeSelectors.setVisibleIf(searchType == SearchType.ARTISTS)
        binding.albumSortTypeSelectors.setVisibleIf(searchType == SearchType.ALBUMS)
        binding.trackSortTypeSelectors.setVisibleIf(searchType == SearchType.TRACKS)
    }

    override fun setArtistSortType(artistSortType: ArtistSortType) {
        binding.artistSortRelevance.isSelected =
            artistSortType == ArtistSortType.RELEVANCE
        binding.artistSortFollowers.isSelected =
            artistSortType == ArtistSortType.FOLLOWERS
        binding.artistSortPopularity.isSelected =
            artistSortType == ArtistSortType.POPULARITY
    }

    override fun setAlbumSortType(albumSortType: AlbumSortType) {
        binding.albumSortRelevance.isSelected =
            albumSortType == AlbumSortType.RELEVANCE
        binding.albumSortPopularity.isSelected =
            albumSortType == AlbumSortType.POPULARITY
        binding.albumSortReleaseDate.isSelected =
            albumSortType == AlbumSortType.RELEASE_DATE
    }

    override fun setTrackSortType(trackSortType: TrackSortType) {
        binding.trackSortRelevance.isSelected =
            trackSortType == TrackSortType.RELEVANCE
        binding.trackSortPopularity.isSelected =
            trackSortType == TrackSortType.POPULARITY
        binding.trackSortLength.isSelected =
            trackSortType == TrackSortType.LENGTH
    }

    override fun showSearchMessage() {
        binding.errorLayout.visibility = View.GONE
        binding.progressIndicator.visibility = View.GONE
        binding.bestsResults.visibility = View.GONE
        binding.artistsRecyclerView.visibility = View.GONE
        binding.albumsRecyclerView.visibility = View.GONE
        binding.tracksRecyclerView.visibility = View.GONE

        binding.messageLayout.visibility = View.VISIBLE
    }

    override fun showErrorMessage() {
        binding.progressIndicator.visibility = View.GONE
        binding.messageLayout.visibility = View.GONE
        binding.bestsResults.visibility = View.GONE
        binding.artistsRecyclerView.visibility = View.GONE
        binding.albumsRecyclerView.visibility = View.GONE
        binding.tracksRecyclerView.visibility = View.GONE

        binding.errorLayout.visibility = View.VISIBLE
    }

    override fun showProgressBar() {
        binding.errorLayout.visibility = View.GONE
        binding.messageLayout.visibility = View.GONE
        binding.bestsResults.visibility = View.GONE
        binding.artistsRecyclerView.visibility = View.GONE
        binding.albumsRecyclerView.visibility = View.GONE
        binding.tracksRecyclerView.visibility = View.GONE

        binding.progressIndicator.visibility = View.VISIBLE
    }

    override fun showBestResults() {
        binding.errorLayout.visibility = View.GONE
        binding.messageLayout.visibility = View.GONE
        binding.progressIndicator.visibility = View.GONE
        binding.artistsRecyclerView.visibility = View.GONE
        binding.albumsRecyclerView.visibility = View.GONE
        binding.tracksRecyclerView.visibility = View.GONE

        binding.bestsResults.visibility = View.VISIBLE
    }

    override fun showArtistsResults() {
        binding.errorLayout.visibility = View.GONE
        binding.messageLayout.visibility = View.GONE
        binding.progressIndicator.visibility = View.GONE
        binding.bestsResults.visibility = View.GONE
        binding.albumsRecyclerView.visibility = View.GONE
        binding.tracksRecyclerView.visibility = View.GONE

        binding.artistsRecyclerView.visibility = View.VISIBLE
    }

    override fun showAlbumsResults() {
        binding.errorLayout.visibility = View.GONE
        binding.messageLayout.visibility = View.GONE
        binding.progressIndicator.visibility = View.GONE
        binding.bestsResults.visibility = View.GONE
        binding.artistsRecyclerView.visibility = View.GONE
        binding.tracksRecyclerView.visibility = View.GONE

        binding.albumsRecyclerView.visibility = View.VISIBLE
    }

    override fun showTracksResults() {
        binding.errorLayout.visibility = View.GONE
        binding.messageLayout.visibility = View.GONE
        binding.progressIndicator.visibility = View.GONE
        binding.bestsResults.visibility = View.GONE
        binding.artistsRecyclerView.visibility = View.GONE
        binding.albumsRecyclerView.visibility = View.GONE

        binding.tracksRecyclerView.visibility = View.VISIBLE
    }

    override fun setBestResults(artist: Artist?, albums: List<Album>, tracks: List<Track>) {
        binding.bestResultsArtist.root.setVisibleIf(artist != null)
        bestsArtistsViewHolder.artist = artist

        bestResultsAlbumAdapter.submitList(albums)
        bestResultsTrackAdapter.submitList(tracks)
    }

    override fun setArtistsResults(artists: List<Artist>) {
        artistAdapter.submitList(artists)
    }

    override fun setAlbumsResults(albums: List<Album>) {
        albumAdapter.submitList(albums)
    }

    override fun setTracksResults(tracks: List<Track>) {
        trackAdapter.submitList(tracks)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
