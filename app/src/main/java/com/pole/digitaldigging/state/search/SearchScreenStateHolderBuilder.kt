package com.pole.digitaldigging.state.search

import com.pole.digitaldigging.state.search.intent.OnArtistClickIntent
import com.pole.domain.usecases.GetSearchResults
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchScreenStateHolderBuilder @Inject constructor(
    private val getSearchResults: GetSearchResults,
) {
    fun build(
        onArtistClickDelegate: OnArtistClickIntent,
    ): SearchScreenStateHolder = SearchScreenStateHolder(
        getSearchResults = getSearchResults,
        onArtistClickDelegate = onArtistClickDelegate
    )
}
