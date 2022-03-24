package com.pole.domain.model

interface SpotifyEntity {
    val id: String
    val spotifyUrl: String?

    val entityType: SpotifyEntityType
}