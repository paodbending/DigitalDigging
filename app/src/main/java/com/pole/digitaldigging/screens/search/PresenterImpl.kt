package com.pole.digitaldigging.screens.search

import androidx.lifecycle.LifecycleOwner
import com.pole.digitaldigging.UIResource

class PresenterImpl(
    private val model: Model,
    private val view: View,
    lifecycleOwner: LifecycleOwner,
) : Presenter {

    init {
        model.results.observe(lifecycleOwner) { results ->
            onResultsChanged(results)
        }
    }

    override fun setSearchQuery(searchQuery: String) {
        model.setSearchQuery(searchQuery)
        view.setSearchQuery(searchQuery)

        if (searchQuery.isEmpty()) view.showSearchMessage()
    }

    override fun setSearchType(searchType: SearchType) {
        model.setSearchSettings(SearchSettings(searchType = searchType))

        // Update selectors
        view.setSearchType(searchType)
        when (searchType) {
            SearchType.ARTISTS -> view.setArtistSortType(ArtistSortType.RELEVANCE)
            SearchType.ALBUMS -> view.setAlbumSortType(AlbumSortType.RELEVANCE)
            SearchType.TRACKS -> view.setTrackSortType(TrackSortType.RELEVANCE)
            else -> {}
        }

        // Show selected results (if ready)
        if (model.searchQuery.isNotEmpty() && model.results.value is UIResource.Ready) when (searchType) {
            SearchType.ALL -> view.showBestResults()
            SearchType.ARTISTS -> view.showArtistsResults()
            SearchType.ALBUMS -> view.showAlbumsResults()
            SearchType.TRACKS -> view.showTracksResults()
        }
    }

    override fun setArtistSortType(artistSortType: ArtistSortType) {
        model.setSearchSettings(model.searchSettings.copy(artistSortType = artistSortType))
        view.setArtistSortType(artistSortType)
    }

    override fun setAlbumSortType(albumSortType: AlbumSortType) {
        model.setSearchSettings(model.searchSettings.copy(albumSortType = albumSortType))
        view.setAlbumSortType(albumSortType)
    }

    override fun setTrackSortType(trackSortType: TrackSortType) {
        model.setSearchSettings(model.searchSettings.copy(trackSortType = trackSortType))
        view.setTrackSortType(trackSortType)
    }

    private fun onResultsChanged(results: UIResource<Results>) {
        when (results) {
            is UIResource.Error -> view.showErrorMessage()
            is UIResource.Loading -> view.showProgressBar()
            is UIResource.Ready -> {

                // Load results
                view.setBestResults(
                    results.value.artists.firstOrNull(),
                    results.value.albums.take(5),
                    results.value.tracks.take(5)
                )
                view.setArtistsResults(
                    when (model.searchSettings.artistSortType) {
                        ArtistSortType.RELEVANCE -> results.value.artists
                        ArtistSortType.POPULARITY -> results.value.artists.sortedByDescending { it.popularity }
                        ArtistSortType.FOLLOWERS -> results.value.artists.sortedByDescending { it.followers }
                    }.filter { it.imageUrl != null && it.followers >= 1000 }
                )
                view.setAlbumsResults(
                    when (model.searchSettings.albumSortType) {
                        AlbumSortType.RELEVANCE -> results.value.albums
                        AlbumSortType.POPULARITY -> results.value.albums.sortedByDescending { it.popularity }
                        AlbumSortType.RELEASE_DATE -> results.value.albums.sortedByDescending { it.releaseDate.toString() }
                    }
                )
                view.setTracksResults(
                    when (model.searchSettings.trackSortType) {
                        TrackSortType.RELEVANCE -> results.value.tracks
                        TrackSortType.POPULARITY -> results.value.tracks.sortedByDescending { it.popularity }
                        TrackSortType.LENGTH -> results.value.tracks.sortedByDescending { it.length }
                    }
                )

                // Show selected results
                if (model.searchQuery.isNotEmpty()) when (model.searchSettings.searchType) {
                    SearchType.ALL -> view.showBestResults()
                    SearchType.ARTISTS -> view.showArtistsResults()
                    SearchType.ALBUMS -> view.showAlbumsResults()
                    SearchType.TRACKS -> view.showTracksResults()
                }
            }
        }
    }
}