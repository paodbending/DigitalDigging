package com.pole.digitaldigging.state.search.intent

import com.pole.digitaldigging.state.search.SearchScreenState

fun interface SetTrackSortByIntent {
    fun setTrackSortType(sortType: SearchScreenState.Tracks.SortBy)
}