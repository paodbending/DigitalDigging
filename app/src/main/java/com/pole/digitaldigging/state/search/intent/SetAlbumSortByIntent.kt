package com.pole.digitaldigging.state.search.intent

import com.pole.digitaldigging.state.search.SearchScreenState

fun interface SetAlbumSortByIntent {
    fun setAlbumSortType(sortType: SearchScreenState.Albums.SortBy)
}