package com.pole.digitaldigging.state.search.intent

import com.pole.digitaldigging.state.search.SearchScreenState

fun interface SetArtistSortByIntent {
    fun setArtistSortType(sortType: SearchScreenState.Artists.SortBy)
}