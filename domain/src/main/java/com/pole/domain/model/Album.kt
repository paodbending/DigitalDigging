package com.pole.domain.model

enum class AlbumType {
    ALBUM,
    SINGLE,
    COMPILATION,
    APPEARS_ON
}

data class Album(
    val spotifyId: String,
    val spotifyUrl: String?,
    val albumType: AlbumType,
    val name: String,
    val type: String,
    val artists: List<Artist>,
    val images: List<Image>,
    val totalTracks: Int? = null,
)


data class AlbumInfo(
    val album: Album,

    val releaseDate: ReleaseDate,
    val genres: List<String>,
    val label: String,
    val popularity: Int,
    val tracks: List<Track>
)