package com.example.digitaldigging.screens.search

import com.pole.domain.model.ArtistInfo

sealed interface SearchState

object Idle: SearchState

object Loading: SearchState

data class SearchResults(
    val artists: List<ArtistInfo>
) : SearchState
