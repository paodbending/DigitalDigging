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
    fun `Setting searchQuery changes the livedata`() = runTest {

        val searchQuery = "Eminem"

        viewModel.search(searchQuery)

        assert(viewModel.searchQuery.getOrAwaitValue() == searchQuery)
    }

    @Test
    fun `Setting SearchType resets all SortTypes`() = runTest {

        viewModel.setAlbumSortType(AlbumSortType.POPULARITY)

        viewModel.setSearchType(SearchType.ALL)

        viewModel.searchSettings.observeForTesting {
            val searchSettings = it.awaitValue()
            assert(searchSettings?.artistSortType == ArtistSortType.RELEVANCE)
            assert(searchSettings?.albumSortType == AlbumSortType.RELEVANCE)
            assert(searchSettings?.trackSortType == TrackSortType.RELEVANCE)
        }
    }

    @Test
    fun `NetworkResource_Ready returns UIResource_Ready`() = runTest {
        viewModel.search("Eminem")

        viewModel.results.observeForTesting {
            val ready = it.awaitValue()
            assert(ready is UIResource.Ready)
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

        viewModel.results.observeForTesting {
            assert(it.awaitValue() is UIResource.Loading)
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
        viewModel.results.observeForTesting {
            val results = it.awaitValue()

            assert(results is UIResource.Error)
            if (results is UIResource.Error)
                assert(results.appError == AppError.NetworkError)
        }
    }
}