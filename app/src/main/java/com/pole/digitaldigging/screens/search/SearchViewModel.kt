package com.pole.digitaldigging.screens.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pole.digitaldigging.DefaultDispatcher
import com.pole.digitaldigging.UIResource
import com.pole.domain.entities.NetworkResource
import com.pole.domain.usecases.GetSearchResults
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getSearchResults: GetSearchResults,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val mutableSearchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> = mutableSearchQuery

    private val mutableSearchSettings = MutableLiveData(SearchSettings(
        searchType = SearchType.ALL,
        artistSortType = ArtistSortType.RELEVANCE,
        albumSortType = AlbumSortType.RELEVANCE,
        trackSortType = TrackSortType.RELEVANCE
    ))
    val searchSettings: LiveData<SearchSettings> = mutableSearchSettings

    private val mutableResults = MutableLiveData<UIResource<Results>>()

    val results: LiveData<UIResource<Results>> = mutableResults

    private var job: Job? = null

    private suspend fun updateResults(searchQuery: String, searchSettings: SearchSettings) {
        if (searchQuery.isEmpty()) {
            mutableResults.postValue(UIResource.Ready(Results(
                artists = emptyList(),
                albums = emptyList(),
                tracks = emptyList()
            )))
        } else {

            delay(250)

            getSearchResults(searchQuery).collectLatest { searchResults ->
                mutableResults.postValue(
                    when (searchResults) {
                        is NetworkResource.Ready -> UIResource.Ready(
                            when (searchSettings.searchType) {
                                SearchType.ALL -> Results(
                                    artists = searchResults.value.artists.take(1),
                                    albums = searchResults.value.albums.take(5),
                                    tracks = searchResults.value.tracks.take(5)
                                )
                                SearchType.ARTISTS -> Results(
                                    artists = when (searchSettings.artistSortType) {
                                        ArtistSortType.RELEVANCE -> searchResults.value.artists
                                        ArtistSortType.POPULARITY -> searchResults.value.artists.sortedByDescending { it.popularity }
                                        ArtistSortType.FOLLOWERS -> searchResults.value.artists.sortedByDescending { it.followers }
                                    }.filter { it.imageUrl != null && it.followers >= 1000 },
                                    albums = emptyList(),
                                    tracks = emptyList()
                                )
                                SearchType.ALBUMS -> Results(
                                    artists = emptyList(),
                                    albums = when (searchSettings.albumSortType) {
                                        AlbumSortType.RELEVANCE -> searchResults.value.albums
                                        AlbumSortType.POPULARITY -> searchResults.value.albums.sortedByDescending { it.popularity }
                                        AlbumSortType.RELEASE_DATE -> searchResults.value.albums.sortedByDescending { it.releaseDate.toString() }
                                    },
                                    tracks = emptyList()
                                )
                                SearchType.TRACKS -> Results(
                                    artists = emptyList(),
                                    albums = emptyList(),
                                    tracks = when (searchSettings.trackSortType) {
                                        TrackSortType.RELEVANCE -> searchResults.value.tracks
                                        TrackSortType.POPULARITY -> searchResults.value.tracks.sortedByDescending { it.popularity }
                                        TrackSortType.LENGTH -> searchResults.value.tracks.sortedByDescending { it.length }
                                    }
                                )
                            }
                        )
                        is NetworkResource.Error -> UIResource.Error(searchResults.appError)
                        is NetworkResource.Loading -> UIResource.Loading()
                    }
                )
            }
        }
    }

    private fun updateResults() {
        job?.cancel()
        job = null
        job = viewModelScope.launch(defaultDispatcher) {
            updateResults(searchQuery.value ?: "",
                searchSettings.value ?: SearchSettings(SearchType.ALL))
        }
    }

    fun setSearchType(searchType: SearchType) {
        mutableSearchSettings.value = SearchSettings(searchType)
        updateResults()
    }

    fun search(searchQuery: String) {
        mutableSearchQuery.value = searchQuery
        updateResults()
    }

    fun setArtistSortType(artistSortType: ArtistSortType) {
        mutableSearchSettings.value = (mutableSearchSettings.value
            ?: SearchSettings(SearchType.ARTISTS)).copy(artistSortType = artistSortType)
        updateResults()
    }

    fun setAlbumSortType(albumSortType: AlbumSortType) {
        mutableSearchSettings.value = (mutableSearchSettings.value
            ?: SearchSettings(SearchType.ALBUMS)).copy(albumSortType = albumSortType)
        updateResults()
    }

    fun setTrackSortType(trackSortType: TrackSortType) {
        mutableSearchSettings.value = (mutableSearchSettings.value
            ?: SearchSettings(SearchType.TRACKS)).copy(trackSortType = trackSortType)
        updateResults()
    }
}