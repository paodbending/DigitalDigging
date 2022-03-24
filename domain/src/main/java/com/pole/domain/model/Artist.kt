package com.pole.domain.model

data class Artist(
    override val id: String,
    override val spotifyUrl: String?,
    val name: String,
    val type: String,
) : SpotifyEntity {
    override val entityType: SpotifyEntityType = SpotifyEntityType.ARTIST
}

data class ArtistInfo(
    val artist: Artist,
    val imageUrl: String?,
    val followers: Int? = null,
    val genres: List<String>,
    val popularity: Int,
) : SpotifyEntity {
    override val id get() = artist.id
    override val spotifyUrl get() = artist.spotifyUrl
    override val entityType: SpotifyEntityType = SpotifyEntityType.ARTIST
    val name get() = artist.name
    val type get() = artist.type
}
