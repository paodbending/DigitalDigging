package com.example.digitaldigging.screens.search

import com.pole.domain.model.spotify.Album
import com.pole.domain.model.spotify.Artist
import com.pole.domain.model.spotify.Track


sealed interface SearchScreenState {

    object Idle : SearchScreenState

    object Loading : SearchScreenState

    object NetworkError : SearchScreenState

    data class Results(
        val query: String,
        val artists: List<Artist>,
        val albums: List<Album>,
        val tracks: List<Track>
    ) : SearchScreenState
}
