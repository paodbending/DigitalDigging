package com.pole.digitaldigging

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.pole.digitaldigging.screens.search.*
import com.pole.domain.AppError
import com.pole.domain.entities.NetworkResource
import com.pole.domain.entities.SearchResult
import com.pole.domain.usecases.GetSearchResults
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private val getSearchResults = GetSearchResults {
        flow {
            emit(NetworkResource.Ready(
                SearchResult(emptyList(),
                    emptyList(),
                    emptyList()
                )
            ))
        }
    }

    private val viewModel: SearchViewModel = SearchViewModel(
        getSearchResults,
        dispatcherRule.dispatcher
    )

    @Test
    fun `Setting searchQuery returns a new state containing the searchQuery`() = runTest {

        val searchQuery = "Eminem"

        viewModel.search(searchQuery)

        viewModel.state.observeForTesting {
            val value = it.awaitValue()
            assert(value != null)
            assert(value?.searchQuery == searchQuery)
        }
    }

    @Test
    fun `Setting SearchType resets all SortTypes`() = runTest {

        viewModel.setAlbumSortType(AlbumSortType.POPULARITY)

        viewModel.setSearchType(SearchType.ALL)

        viewModel.state.observeForTesting {
            val state = it.awaitValue()
            assert(state != null)
            assert(state?.searchSettings?.artistSortType == ArtistSortType.RELEVANCE)
            assert(state?.searchSettings?.albumSortType == AlbumSortType.RELEVANCE)
            assert(state?.searchSettings?.trackSortType == TrackSortType.RELEVANCE)
        }
    }

    @Test
    fun `NetworkResource_Ready returns UIResource_Ready`() = runTest {
        viewModel.search("Eminem")

        viewModel.state.observeForTesting {
            val readyState = it.awaitValue()
            assert(readyState?.results is UIResource.Ready)
        }
    }

    @Test
    fun `NetworkResource_Loading returns UIResource_Loading`() = runTest {

        val getSearchResults = GetSearchResults {
            flow {
                emit(NetworkResource.Loading())
            }
        }

        val viewModel = SearchViewModel(getSearchResults, dispatcherRule.dispatcher)

        viewModel.search("Eminem")

        viewModel.state.observeForTesting {
            assert(it.awaitValue()?.results is UIResource.Loading)
        }
    }

    @Test
    fun `NetworkResource_Error returns UIResource_Error`() = runTest {

        val getSearchResults = GetSearchResults {
            flow {
                emit(NetworkResource.Error(AppError.NetworkError))
            }
        }

        val viewModel = SearchViewModel(getSearchResults, dispatcherRule.dispatcher)

        viewModel.search("Eminem")
        viewModel.state.observeForTesting {
            val state = it.awaitValue()

            assert(state != null)
            assert(state?.results is UIResource.Error)
            val results = state?.results
            if (results is UIResource.Error)
                assert(results.appError == AppError.NetworkError)
        }
    }
}