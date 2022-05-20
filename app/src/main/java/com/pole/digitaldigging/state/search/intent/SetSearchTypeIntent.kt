package com.pole.digitaldigging.state.search.intent

fun interface SetSearchTypeIntent {
    fun setSearchType(searchType: SearchType)

    enum class SearchType {
        BEST_RESULTS, ARTISTS, ALBUMS, TRACKS
    }
}