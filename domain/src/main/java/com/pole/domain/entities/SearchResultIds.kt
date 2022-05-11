package com.pole.domain.entities

data class SearchResultIds(
    val artistsIds: List<String> = emptyList(),
    val albumsIds: List<String> = emptyList(),
    val tracksIds: List<String> = emptyList()
)