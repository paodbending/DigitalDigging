package com.pole.digitaldigging

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.pole.digitaldigging.screens.search.ModelImpl
import com.pole.domain.AppError
import com.pole.domain.entities.NetworkResource
import com.pole.domain.entities.SearchResult
import com.pole.domain.usecases.GetSearchResults
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private val coroutineScope = TestScope(dispatcherRule.dispatcher)

    private val getSearchResults = GetSearchResults {
        flow {
            emit(NetworkResource.Ready(
                SearchResult(
                    listOf(FakeData.artist),
                    listOf(FakeData.album),
                    listOf(FakeData.track)
                )
            ))
        }
    }

    private val model = ModelImpl(
        getSearchResults = getSearchResults,
        coroutineScope = coroutineScope,
        defaultDispatcher = dispatcherRule.dispatcher
    )

    @Test
    fun `Empty searchQuery returns empty results`() = runTest {
        model.results.observeForTesting {
            model.setSearchQuery("")
            val results = it.awaitValue()
            assert(results is UIResource.Ready)
            if (results is UIResource.Ready) {
                assert(results.value.artists.isEmpty())
                assert(results.value.albums.isEmpty())
                assert(results.value.tracks.isEmpty())
            }
        }
    }

    @Test
    fun `NetworkResource_Ready returns UIResource_Ready`() = runTest {
        model.results.observeForTesting {
            model.setSearchQuery("Eminem")
            val results = it.awaitValue()
            assert(results is UIResource.Ready)
            if (results is UIResource.Ready) {
                assert(results.value.artists.firstOrNull() == FakeData.artist)
                assert(results.value.albums.firstOrNull() == FakeData.album)
                assert(results.value.tracks.firstOrNull() == FakeData.track)
            }
        }
    }

    @Test
    fun `NetworkResource_Loading returns UIResource_Loading`() = runTest {

        val getSearchResults = GetSearchResults {
            flow {
                emit(NetworkResource.Loading())
            }
        }

        val model = ModelImpl(getSearchResults, coroutineScope, dispatcherRule.dispatcher)

        model.results.observeForTesting {
            model.setSearchQuery("Eminem")
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

        val model = ModelImpl(getSearchResults, coroutineScope, dispatcherRule.dispatcher)

        model.results.observeForTesting {
            model.setSearchQuery("Eminem")

            val results = it.awaitValue()
            assert(results is UIResource.Error)
            if (results is UIResource.Error)
                assert(results.appError == AppError.NetworkError)
        }
    }
}