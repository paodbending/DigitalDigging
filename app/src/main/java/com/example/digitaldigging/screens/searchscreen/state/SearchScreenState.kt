package com.example.digitaldigging.screens.searchscreen.state

import com.example.digitaldigging.screens.searchscreen.state.data.ArtistsData
import com.example.digitaldigging.screens.searchscreen.state.data.SearchQueryData
import com.example.digitaldigging.screens.searchscreen.state.intents.OnArtistClickIntent
import com.example.digitaldigging.screens.searchscreen.state.intents.SetSearchQueryIntent
import com.pole.domain.model.spotify.Artist

/**
 * State classes for the Search Screen.
 *
 * In general, State classes should be as abstract as possible.
 * They are the bridge between the View Layer and the Logic layer of the application.
 */
sealed interface SearchScreenState : SetSearchQueryIntent {

    data class Idle(
        private val setSearchQueryDelegate: SetSearchQueryIntent,
    ) : SearchScreenState, SetSearchQueryIntent by setSearchQueryDelegate

    data class Loading(
        override val searchQuery: String,
        private val setSearchQueryDelegate: SetSearchQueryIntent,
    ) : SearchScreenState, SetSearchQueryIntent by setSearchQueryDelegate, SearchQueryData

    data class NetworkError(
        override val searchQuery: String,
        private val setSearchQueryDelegate: SetSearchQueryIntent,
    ) : SearchScreenState, SetSearchQueryIntent by setSearchQueryDelegate, SearchQueryData

    data class Results(
        override val searchQuery: String,
        override val artists: List<Artist>,
        private val setSearchQueryDelegate: SetSearchQueryIntent,
        private val onArtistClickDelegate: OnArtistClickIntent,
    ) : SearchScreenState,
        SearchQueryData,
        ArtistsData,
        SetSearchQueryIntent by setSearchQueryDelegate,
        OnArtistClickIntent by onArtistClickDelegate
}
