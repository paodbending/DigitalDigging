package com.pole.domain.model.spotify

enum class AlbumType {
    UNKNOWN,
    ALBUM,
    SINGLE
}

data class Album(
    val id: String,
    val spotifyUrl: String?,
    val albumType: AlbumType,
    val name: String,
    val type: String,
    val artistIds: List<String>,
    val artistNames: List<String>,
    val imageUrl: String?,
    val totalTracks: Int? = null,
    val releaseDate: ReleaseDate,
    val genres: List<String>,
    val label: String,
    val popularity: Int,
)