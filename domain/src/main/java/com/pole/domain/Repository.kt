package com.pole.domain

import com.pole.domain.model.*
import javax.inject.Singleton

@Singleton
interface Repository {

    suspend fun searchArtist(query: String): List<ArtistInfo>
    suspend fun getArtistInfo(spotifyId: String): ArtistInfo?
    suspend fun getArtistAlbums(spotifyId: String): List<Album>
    suspend fun getRelatedArtistsInfo(spotifyId: String): List<ArtistInfo>

    suspend fun getAlbumInfo(spotifyId: String): AlbumInfo?
    suspend fun getAlbumTracks(spotifyId: String): List<Track>


    suspend fun getTrackInfo(spotifyId: String): TrackInfo?
}