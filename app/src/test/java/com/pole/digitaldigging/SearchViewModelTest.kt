package com.pole.digitaldigging

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.pole.digitaldigging.screens.search.*
import com.pole.domain.AppError
import com.pole.domain.entities.NetworkResource
import com.pole.domain.entities.SearchResult
import com.pole.domain.usecases.GetSearchResults
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private val testDispatcher = dispatcherRule.dispatcher

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
        getSearchResults
    )

    private inline fun SearchViewModel.observe(
        coroutineScope: CoroutineScope,
        testBody: () -> Unit,
    ) {
        val job =
            coroutineScope.launch(testDispatcher) { collectState() }
        testBody()
        job.cancel()
    }

    private fun SearchViewModel.awaitState(
        testScope: TestScope,
    ): SearchScreenState {
        val job =
            testScope.launch(testDispatcher) { collectState() }
        testScope.advanceUntilIdle()
        job.cancel()
        return state.value
    }

    @Test
    fun `Setting searchQuery returns a new state containing the searchQuery`() = runTest {
        val searchQuery = "Eminem"
        viewModel.search(searchQuery)
        val state = viewModel.awaitState(this)
        assert(state.searchQuery == searchQuery)
    }

    @Test
    fun `Setting SearchType resets all SortTypes`() = runTest {
        viewModel.setAlbumSortType(AlbumSortType.POPULARITY)
        viewModel.setSearchType(SearchType.ALL)
        val state = viewModel.awaitState(this)
        assert(state.searchSettings.artistSortType == ArtistSortType.RELEVANCE)
        assert(state.searchSettings.albumSortType == AlbumSortType.RELEVANCE)
        assert(state.searchSettings.trackSortType == TrackSortType.RELEVANCE)
    }

    @Test
    fun `NetworkResource_Ready returns UIResource_Ready`() = runTest {
        viewModel.search("Eminem")
        assert(viewModel.awaitState(this).results is UIResource.Ready)
    }

    @Test
    fun `NetworkResource_Loading returns UIResource_Loading`() = runTest {
        val getSearchResults = GetSearchResults {
            flow {
                emit(NetworkResource.Loading())
            }
        }
        val viewModel = SearchViewModel(getSearchResults)
        viewModel.search("Eminem")
        assert(viewModel.awaitState(this).results is UIResource.Loading)
    }

    @Test
    fun `NetworkResource_Error returns UIResource_Error`() = runTest {
        val getSearchResults = GetSearchResults {
            flow {
                emit(NetworkResource.Error(AppError.NetworkError))
            }
        }
        val viewModel = SearchViewModel(getSearchResults)
        viewModel.search("Eminem")
        val results = viewModel.awaitState(this).results
        assert(results is UIResource.Error)
        if (results is UIResource.Error)
            assert(results.appError == AppError.NetworkError)
    }
}