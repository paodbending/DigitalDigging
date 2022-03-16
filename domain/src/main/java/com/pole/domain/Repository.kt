package com.pole.domain

import com.pole.domain.model.Album
import com.pole.domain.model.ArtistInfo

interface Repository {
    suspend fun searchArtist(query: String): List<ArtistInfo>
    suspend fun getArtist(spotifyId: String): ArtistInfo?
    suspend fun getArtistAlbums(spotifyId: String): List<Album>
    suspend fun getRelatedArtists(spotifyId: String): List<ArtistInfo>
}