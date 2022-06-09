package com.pole.digitaldigging

import androidx.annotation.StringRes
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.pole.digitaldigging.screens.search.SearchScreen
import com.pole.digitaldigging.screens.search.SearchViewModel
import com.pole.domain.entities.NetworkResource
import com.pole.domain.entities.SearchResult
import com.pole.domain.usecases.GetSearchResults
import kotlinx.coroutines.flow.flow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SearchScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

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


    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private fun getString(@StringRes id: Int): String {
        return context.getString(id)
    }

    @Before
    fun setup() {
        composeTestRule.setContent {
            SearchScreen(
                viewModel = viewModel
            )
        }
    }

    @Test
    fun testSearchQuery() {
        val searchQuery = "Eminem"
        composeTestRule
            .onNodeWithTag("searchQuery")
            .performTextReplacement(searchQuery)

        composeTestRule
            .onNodeWithTag("searchQuery")
            .assertTextEquals(searchQuery)
    }

    @Test
    fun testBestResultsSearchSettings() {
        composeTestRule
            .onNodeWithText(getString(R.string.best_results))
            .performClick()

        composeTestRule
            .onNodeWithText(getString(R.string.relevance))
            .assertDoesNotExist()

        composeTestRule
            .onNodeWithText(getString(R.string.popularity))
            .assertDoesNotExist()

        composeTestRule
            .onNodeWithText(getString(R.string.release_date))
            .assertDoesNotExist()

        composeTestRule
            .onNodeWithText(getString(R.string.followers))
            .assertDoesNotExist()

        composeTestRule
            .onNodeWithText(getString(R.string.length))
            .assertDoesNotExist()
    }

    @Test
    fun testArtistSearchSettings() {
        composeTestRule
            .onNodeWithText(getString(R.string.artists))
            .performClick()

        composeTestRule
            .onNodeWithText(getString(R.string.relevance))
            .assertExists()

        composeTestRule
            .onNodeWithText(getString(R.string.popularity))
            .assertExists()

        composeTestRule
            .onNodeWithText(getString(R.string.followers))
            .assertExists()
    }

    @Test
    fun testAlbumsSearchSettings() {
        composeTestRule
            .onNodeWithText(getString(R.string.albums))
            .performClick()

        composeTestRule
            .onNodeWithText(getString(R.string.relevance))
            .assertExists()

        composeTestRule
            .onNodeWithText(getString(R.string.popularity))
            .assertExists()

        composeTestRule
            .onNodeWithText(getString(R.string.release_date))
            .assertExists()
    }

    @Test
    fun testTracksSearchSettings() {
        composeTestRule
            .onNodeWithText(getString(R.string.tracks))
            .performClick()

        composeTestRule
            .onNodeWithText(getString(R.string.relevance))
            .assertExists()

        composeTestRule
            .onNodeWithText(getString(R.string.popularity))
            .assertExists()

        composeTestRule
            .onNodeWithText(getString(R.string.length))
            .assertExists()
    }
}