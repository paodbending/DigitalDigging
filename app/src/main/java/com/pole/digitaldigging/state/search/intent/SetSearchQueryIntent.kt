package com.pole.digitaldigging.state.search.intent

fun interface SetSearchQueryIntent {
    fun setSearchQuery(searchQuery: String)
}