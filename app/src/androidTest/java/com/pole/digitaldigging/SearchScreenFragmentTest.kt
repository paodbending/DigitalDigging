package com.pole.digitaldigging

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pole.digitaldigging.screens.search.SearchScreenFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class SearchScreenFragmentTest {

    @Before
    fun before() {
        launchFragmentInHiltContainer<SearchScreenFragment>()
    }

    @Test
    fun testSearchQuery() {
        onView(withId(R.id.search_edit_text))
            .perform(clearText())
            .check(matches(withText("")))

        onView(withId(R.id.message_layout))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        val text = "Eminem"

        onView(withId(R.id.search_edit_text))
            .perform(replaceText(text))
            .check(matches(withText(text)))

        runBlocking { delay(300) }

        onView(withId(R.id.message_layout))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))

        onView(withId(R.id.search_edit_text))
            .perform(pressBack())
            .check(matches(withText("")))

        onView(withId(R.id.message_layout))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun testBestResultsSearchSettings() {
        onView(withId(R.id.search_type_artists))
            .perform(click())

        onView(withId(R.id.search_type_bests_results))
            .check(matches(not(isSelected())))
            .perform(click())
            .check(matches(isSelected()))
    }

    @Test
    fun testArtistSearchSettings() {
        onView(withId(R.id.search_type_bests_results))
            .perform(click())

        onView(withId(R.id.artist_sort_type_selectors))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))

        onView(withId(R.id.search_type_artists))
            .perform(click())
            .check(matches(isSelected()))

        onView(withId(R.id.artist_sort_type_selectors))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView(withId(R.id.artist_sort_relevance))
            .check(matches(isSelected()))

        onView(withId(R.id.artist_sort_popularity))
            .perform(click())
            .check(matches(isSelected()))

        onView(withId(R.id.artist_sort_followers))
            .perform(click())
            .check(matches(isSelected()))
    }

    @Test
    fun testAlbumsSearchSettings() {
        onView(withId(R.id.search_type_bests_results))
            .perform(click())

        onView(withId(R.id.album_sort_type_selectors))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))

        onView(withId(R.id.search_type_albums))
            .perform(click())
            .check(matches(isSelected()))

        onView(withId(R.id.album_sort_type_selectors))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView(withId(R.id.album_sort_relevance))
            .check(matches(isSelected()))

        onView(withId(R.id.album_sort_popularity))
            .perform(click())
            .check(matches(isSelected()))

        onView(withId(R.id.album_sort_release_date))
            .perform(click())
            .check(matches(isSelected()))
    }

    @Test
    fun testTracksSearchSettings() {
        onView(withId(R.id.search_type_bests_results))
            .perform(click())

        onView(withId(R.id.track_sort_type_selectors))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))

        onView(withId(R.id.search_type_tracks))
            .perform(click())
            .check(matches(isSelected()))

        onView(withId(R.id.track_sort_type_selectors))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView(withId(R.id.track_sort_relevance))
            .check(matches(isSelected()))

        onView(withId(R.id.track_sort_popularity))
            .perform(click())
            .check(matches(isSelected()))

        onView(withId(R.id.track_sort_length))
            .perform(click())
            .check(matches(isSelected()))
    }
}