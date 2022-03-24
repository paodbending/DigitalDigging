package com.pole.domain.model

enum class AlbumType {
    ALBUM,
    SINGLE,
    COMPILATION,
    APPEARS_ON
}

data class Album(
    val id: String,
    val spotifyUrl: String?,
    val albumType: AlbumType,
    val name: String,
    val type: String,
    val artists: List<Artist>,
    val imageUrl: String?,
    val totalTracks: Int? = null,
)

data class AlbumInfo(
    val album: Album,
    val releaseDate: ReleaseDate,
    val genres: List<String>,
    val label: String,
    val popularity: Int,
    val tracks: List<Track>
) {
    val id get() = album.id
    val spotifyUrl get() = album.spotifyUrl
    val albumType get() = album.albumType
    val name get() = album.name
    val type get() = album.type
    val artists get() = album.artists
    val imageUrl get() = album.imageUrl
    val totalTracks get() = album.totalTracks
}