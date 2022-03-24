package com.example.digitaldigging.screens.search

import com.pole.domain.model.ArtistInfo

sealed interface SearchState

object Idle : SearchState

data class Loading(
    val query: String
) : SearchState

data class SearchResults(
    val query: String,
    val artists: List<ArtistInfo>
) : SearchState
