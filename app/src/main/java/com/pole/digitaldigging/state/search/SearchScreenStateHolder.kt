package com.pole.digitaldigging.state.search

import com.pole.digitaldigging.UIResource
import com.pole.digitaldigging.state.search.intent.*
import com.pole.digitaldigging.vhs.StateHolder
import com.pole.domain.entities.NetworkResource
import com.pole.domain.usecases.GetSearchResults
import kotlinx.coroutines.flow.map

class SearchScreenStateHolder internal constructor(
    private val getSearchResults: GetSearchResults,
    private val onArtistClickDelegate: OnArtistClickIntent,
) : StateHolder<SearchScreenState>(), SetSearchQueryIntent,
    SetSearchTypeIntent,
    SetArtistSortByIntent,
    SetAlbumSortByIntent,
    SetTrackSortByIntent {

    override fun buildInitialState(): SearchScreenState = SearchScreenState.BestResults(
        searchQuery = "",
        results = UIResource.Loading(),
        onArtistClickDelegate = onArtistClickDelegate,
        setSearchTypeDelegate = this,
        setSearchQueryDelegate = this,
    )

    private fun getBestResultsFlow(
        searchQuery: String = value.searchQuery,
    ) = getSearchResults(searchQuery).map { searchResults ->
        SearchScreenState.BestResults(
            onArtistClickDelegate = onArtistClickDelegate,
            setSearchTypeDelegate = this,
            setSearchQueryDelegate = this,
            searchQuery = searchQuery,
            results = when (searchResults) {
                is NetworkResource.Ready -> UIResource.Ready(
                    SearchScreenState.BestResults.Results(
                        artist = searchResults.value.artists.firstOrNull { it.imageUrl != null && it.followers >= 1000 },
                        albums = searchResults.value.albums,
                        tracks = searchResults.value.tracks
                    )
                )
                is NetworkResource.Error -> UIResource.Error(searchResults.appError)
                is NetworkResource.Loading -> UIResource.Loading()
            }
        )
    }

    private fun getArtistsFlow(
        searchQuery: String = value.searchQuery,
        sortBy: SearchScreenState.Artists.SortBy = SearchScreenState.Artists.SortBy.RELEVANCE,
    ) = getSearchResults(searchQuery).map { searchResults ->
        SearchScreenState.Artists(
            onArtistClickDelegate = onArtistClickDelegate,
            setSearchTypeDelegate = this,
            setSearchQueryDelegate = this,
            setArtistSortByDelegate = this,
            searchQuery = searchQuery,
            sortBy = sortBy,
            artists = when (searchResults) {
                is NetworkResource.Ready -> {
                    UIResource.Ready(when (sortBy) {
                        SearchScreenState.Artists.SortBy.RELEVANCE -> searchResults.value.artists.filter { it.imageUrl != null && it.followers >= 1000 }
                        SearchScreenState.Artists.SortBy.POPULARITY -> searchResults.value.artists.sortedByDescending { it.popularity }
                        SearchScreenState.Artists.SortBy.FOLLOWERS -> searchResults.value.artists.sortedByDescending { it.followers }
                    })
                }
                is NetworkResource.Error -> UIResource.Error(searchResults.appError)
                is NetworkResource.Loading -> UIResource.Loading()
            }
        )
    }

    private fun getAlbumsFlow(
        searchQuery: String = value.searchQuery,
        sortBy: SearchScreenState.Albums.SortBy = SearchScreenState.Albums.SortBy.RELEVANCE,
    ) = getSearchResults(searchQuery).map { searchResults ->
        SearchScreenState.Albums(
            setSearchTypeDelegate = this,
            setSearchQueryDelegate = this,
            setAlbumSortByDelegate = this,
            searchQuery = searchQuery,
            sortBy = sortBy,
            albums = when (searchResults) {
                is NetworkResource.Ready -> {
                    UIResource.Ready(when (sortBy) {
                        SearchScreenState.Albums.SortBy.RELEVANCE -> searchResults.value.albums
                        SearchScreenState.Albums.SortBy.POPULARITY -> searchResults.value.albums.sortedByDescending { it.popularity }
                        SearchScreenState.Albums.SortBy.RELEASE_DATE -> searchResults.value.albums.sortedByDescending { it.releaseDate.toString() }
                    })
                }
                is NetworkResource.Error -> UIResource.Error(searchResults.appError)
                is NetworkResource.Loading -> UIResource.Loading()
            }
        )
    }

    private fun getTracksFlow(
        searchQuery: String = value.searchQuery,
        sortBy: SearchScreenState.Tracks.SortBy = SearchScreenState.Tracks.SortBy.RELEVANCE,
    ) = getSearchResults(searchQuery).map { searchResults ->
        SearchScreenState.Tracks(
            setSearchTypeDelegate = this,
            setSearchQueryDelegate = this,
            setTrackSortByDelegate = this,
            searchQuery = searchQuery,
            sortBy = sortBy,
            tracks = when (searchResults) {
                is NetworkResource.Ready -> {
                    UIResource.Ready(when (sortBy) {
                        SearchScreenState.Tracks.SortBy.RELEVANCE -> searchResults.value.tracks
                        SearchScreenState.Tracks.SortBy.POPULARITY -> searchResults.value.tracks.sortedByDescending { it.popularity }
                        SearchScreenState.Tracks.SortBy.LENGTH -> searchResults.value.tracks.sortedByDescending { it.length }
                    })
                }
                is NetworkResource.Error -> UIResource.Error(searchResults.appError)
                is NetworkResource.Loading -> UIResource.Loading()
            }
        )
    }

    override fun setSearchQuery(searchQuery: String) {
        setStateGenerator(when (value) {
            is SearchScreenState.Albums -> getAlbumsFlow(searchQuery = searchQuery)
            is SearchScreenState.Artists -> getArtistsFlow(searchQuery = searchQuery)
            is SearchScreenState.BestResults -> getBestResultsFlow(searchQuery = searchQuery)
            is SearchScreenState.Tracks -> getTracksFlow(searchQuery = searchQuery)
        })
    }

    override fun setSearchType(searchType: SetSearchTypeIntent.SearchType) {
        when {
            searchType == SetSearchTypeIntent.SearchType.BEST_RESULTS && value !is SearchScreenState.BestResults -> {
                setStateGenerator(getBestResultsFlow())
            }
            searchType == SetSearchTypeIntent.SearchType.ARTISTS && value !is SearchScreenState.Artists -> {
                setStateGenerator(getArtistsFlow())
            }
            searchType == SetSearchTypeIntent.SearchType.ALBUMS && value !is SearchScreenState.Albums -> {
                setStateGenerator(getAlbumsFlow())
            }
            searchType == SetSearchTypeIntent.SearchType.TRACKS && value !is SearchScreenState.Tracks -> {
                setStateGenerator(getTracksFlow())
            }
        }
    }

    override fun setAlbumSortType(sortType: SearchScreenState.Albums.SortBy) {
        setStateGenerator(getAlbumsFlow(sortBy = sortType))
    }

    override fun setArtistSortType(sortType: SearchScreenState.Artists.SortBy) {
        setStateGenerator(getArtistsFlow(sortBy = sortType))
    }

    override fun setTrackSortType(sortType: SearchScreenState.Tracks.SortBy) {
        setStateGenerator(getTracksFlow(sortBy = sortType))
    }
}
