package com.pole.domain.model.spotify

data class SearchResult(
    val artistIds: List<String> = emptyList(),
    val albumIds: List<String> = emptyList(),
    val trackIds: List<String> = emptyList()
)