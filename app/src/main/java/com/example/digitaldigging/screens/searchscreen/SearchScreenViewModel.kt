package com.example.digitaldigging.screens.searchscreen

import com.example.digitaldigging.screens.searchscreen.state.SearchScreenState
import com.example.digitaldigging.screens.searchscreen.state.intents.SetSearchQueryIntent
import com.example.digitaldigging.vhs.ViewModelStateHolder
import com.pole.domain.model.NetworkResource
import com.pole.domain.usecases.spotify.GetArtists
import com.pole.domain.usecases.spotify.GetSearchResults
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    private val getSearchResults: GetSearchResults,
    private val getArtists: GetArtists,
) : ViewModelStateHolder<SearchScreenState>(), SetSearchQueryIntent {

    override fun getInitialState(): SearchScreenState = SearchScreenState.Idle(this)

    override fun setSearchQuery(newSearchQuery: String) = setStateGenerator {
        // If searchQuery is empty, emit the Idle State
        if (newSearchQuery.isEmpty()) {
            emit(SearchScreenState.Idle(setSearchQueryDelegate = this@SearchScreenViewModel))
        } else {
            // If searchQuery is not empty, emit the Loading State
            emit(
                SearchScreenState.Loading(
                    searchQuery = newSearchQuery,
                    setSearchQueryDelegate = this@SearchScreenViewModel
                )
            )

            // Wait for the user to stop typing
            delay(250)

            // Collect results from UseCase
            getSearchResults(newSearchQuery).collect { searchResults ->
                when (searchResults) {
                    is NetworkResource.Loading -> emit(
                        SearchScreenState.Loading(
                            searchQuery = newSearchQuery,
                            setSearchQueryDelegate = this@SearchScreenViewModel
                        )
                    )
                    is NetworkResource.Error -> emit(
                        SearchScreenState.NetworkError(
                            searchQuery = newSearchQuery,
                            setSearchQueryDelegate = this@SearchScreenViewModel
                        )
                    )
                    is NetworkResource.Ready -> {
                        // Collect results from UseCase
                        getArtists(searchResults.value.artistIds.toSet()).collect { artistsResource ->
                            val artists = when (artistsResource) {
                                is NetworkResource.Ready -> {
                                    val indexes =
                                        searchResults.value.artistIds.mapIndexed { i, e -> e to i }
                                            .toMap()
                                    artistsResource.value.sortedBy { indexes[it.id] }
                                }
                                else -> emptyList()
                            }
                            emit(
                                SearchScreenState.Results(
                                    searchQuery = newSearchQuery,
                                    artists = artists,
                                    setSearchQueryDelegate = this@SearchScreenViewModel,
                                    onArtistClickDelegate = {
                                        // Just for this example, navigation logic is implemented
                                        // directly inside the View.
                                    }
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}