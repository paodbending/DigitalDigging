package com.example.digitaldigging.screens.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.digitaldigging.UIResource
import com.pole.domain.model.NetworkResource
import com.pole.domain.usecases.spotify.GetSearchResults
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getSearchResults: GetSearchResults,
) : ViewModel() {

    private val searchQueryFlow = MutableStateFlow("")

    private val searchSettingFlow = MutableStateFlow(SearchSettings(
        searchType = SearchType.ALL,
        artistSortType = ArtistSortType.RELEVANCE,
        albumSortType = AlbumSortType.RELEVANCE,
        trackSortType = TrackSortType.RELEVANCE
    ))

    val state: LiveData<SearchScreenState> = liveData(Dispatchers.Default) {
        searchQueryFlow.collectLatest { searchQuery ->

            if (searchQuery.isEmpty()) {
                searchSettingFlow.collectLatest { searchSettings ->
                    emit(
                        SearchScreenState(
                            searchQuery = searchQuery,
                            searchSettings = searchSettings,
                            results = UIResource.Loading()
                        )
                    )
                }
            } else {

                delay(250)

                getSearchResults(searchQuery).collectLatest { searchResults ->

                    searchSettingFlow.collectLatest { searchSettings ->

                        emit(SearchScreenState(
                            searchQuery = searchQuery,
                            searchSettings = searchSettings,
                            results = when (searchResults) {
                                is NetworkResource.Ready -> UIResource.Ready(
                                    when (searchSettings.searchType) {
                                        SearchType.ALL -> Results(
                                            artists = searchResults.value.artists,
                                            albums = searchResults.value.albums,
                                            tracks = searchResults.value.tracks
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
                        ))
                    }
                }
            }
        }
    }

    fun setSearchType(searchType: SearchType) {
        searchSettingFlow.value = SearchSettings(searchType)
    }

    fun search(searchQuery: String) {
        searchQueryFlow.value = searchQuery
    }

    fun setArtistSortType(artistSortType: ArtistSortType) {
        searchSettingFlow.value = searchSettingFlow.value.copy(artistSortType = artistSortType)
    }

    fun setAlbumSortType(albumSortType: AlbumSortType) {
        searchSettingFlow.value = searchSettingFlow.value.copy(albumSortType = albumSortType)
    }

    fun setTrackSortType(trackSortType: TrackSortType) {
        searchSettingFlow.value = searchSettingFlow.value.copy(trackSortType = trackSortType)
    }
}