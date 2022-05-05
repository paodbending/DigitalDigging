package com.example.digitaldigging.screens.search

import androidx.lifecycle.*
import com.pole.domain.model.NetworkResource
import com.pole.domain.usecases.spotify.GetAlbums
import com.pole.domain.usecases.spotify.GetArtists
import com.pole.domain.usecases.spotify.GetSearchResults
import com.pole.domain.usecases.spotify.GetTracks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getSearchResults: GetSearchResults,
    private val getArtists: GetArtists,
    private val getAlbums: GetAlbums,
    private val getTracks: GetTracks
) : ViewModel() {

    private val searchQuery = MutableLiveData<String>()

    val screenState: LiveData<SearchScreenState> =
        searchQuery.distinctUntilChanged().switchMap { searchQuery ->
            liveData(context = Dispatchers.Default) {

                if (searchQuery.isNullOrEmpty()) {
                    emit(SearchScreenState.Idle)
                } else {

                    emit(SearchScreenState.Loading)

                    delay(250)

                    getSearchResults(searchQuery).collect { searchResults ->
                        when (searchResults) {
                            is NetworkResource.Loading -> emit(SearchScreenState.Loading)
                            is NetworkResource.Error -> emit(SearchScreenState.NetworkError)
                            is NetworkResource.Ready -> {
                                combine(
                                    getArtists(searchResults.value.artistIds.toSet()),
                                    getAlbums(searchResults.value.albumIds.toSet()),
                                    getTracks(searchResults.value.trackIds.toSet())
                                ) { artistsResource, albumsResource, tracksResource ->

                                    val artists = when (artistsResource) {
                                        is NetworkResource.Ready -> {
                                            val indexes =
                                                searchResults.value.artistIds.mapIndexed { i, e -> e to i }
                                                    .toMap()
                                            artistsResource.value.sortedBy { indexes[it.id] }
                                        }
                                        else -> emptyList()
                                    }

                                    val albums = when (albumsResource) {
                                        is NetworkResource.Ready -> {
                                            val indexes =
                                                searchResults.value.albumIds.mapIndexed { i, e -> e to i }
                                                    .toMap()
                                            albumsResource.value.sortedBy { indexes[it.id] }
                                        }
                                        else -> emptyList()
                                    }

                                    val tracks = when (tracksResource) {
                                        is NetworkResource.Ready -> {
                                            val indexes =
                                                searchResults.value.trackIds.mapIndexed { i, e -> e to i }
                                                    .toMap()
                                            tracksResource.value.sortedBy { indexes[it.id] }
                                        }
                                        else -> emptyList()
                                    }

                                    SearchScreenState.Results(
                                        searchQuery,
                                        artists = artists,
                                        albums = albums,
                                        tracks = tracks
                                    )
                                }.collect { emit(it) }
                            }
                        }
                    }
                }
            }
        }

    fun search(query: String) {
        searchQuery.value = query
    }
}