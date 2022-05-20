package com.pole.digitaldigging.state.main

import androidx.compose.runtime.State
import com.pole.digitaldigging.state.artist.ArtistScreenState
import com.pole.digitaldigging.state.main.intent.NavigateToArtistScreenIntent
import com.pole.digitaldigging.state.search.SearchScreenState

interface MainState {

    data class SearchScreen(
        val searchScreenState: State<SearchScreenState>,
        private val navigateToArtistPageDelegate: NavigateToArtistScreenIntent,
    ) : MainState, NavigateToArtistScreenIntent by navigateToArtistPageDelegate

    data class ArtistScreen(
        val stateHolder: State<ArtistScreenState>,
    ) : MainState
}