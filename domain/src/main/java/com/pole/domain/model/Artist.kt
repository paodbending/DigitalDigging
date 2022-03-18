package com.pole.domain.model


data class Artist(
    val spotifyId: String,
    val spotifyUrl: String?,
    val name: String,
    val type: String,
)

data class ArtistInfo(
    val artist: Artist,
    val image: Image?,
    val followers: Int? = null,
    val genres: List<String>,
    val popularity: Int,
)
