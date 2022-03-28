package com.pole.domain.model.spotify

data class Artist(
    val id: String,
    val spotifyUrl: String?,
    val name: String,
    val type: String,
    val imageUrl: String?,
    val followers: Int? = null,
    val genres: List<String>,
    val popularity: Int,
)
