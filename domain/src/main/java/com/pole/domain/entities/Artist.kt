package com.pole.domain.entities

data class Artist(
    val id: String,
    val spotifyUrl: String?,
    val name: String,
    val type: String,
    val imageUrl: String?,
    val followers: Int,
    val genres: List<String>,
    val popularity: Int,
)
