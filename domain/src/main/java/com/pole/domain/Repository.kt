package com.pole.domain

import com.pole.domain.model.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
interface Repository {

    suspend fun setup()

    // Artist
    suspend fun searchArtist(query: String): List<ArtistInfo>
    suspend fun getArtistInfo(id: String): ArtistInfo?
    suspend fun getArtistAlbums(id: String): List<Album>
    suspend fun getRelatedArtistsInfo(id: String): List<ArtistInfo>

    // Album
    suspend fun getAlbumInfo(id: String): AlbumInfo?
    suspend fun getAlbumTracks(id: String): List<Track>

    // Track
    suspend fun getTrackInfo(id: String): TrackInfo?

    // User Data
    suspend fun getUserData(entity: SpotifyEntity): SpotifyEntityUserData
    suspend fun setLibrary(entity: SpotifyEntity, boolean: Boolean)
    suspend fun setScheduled(entity: SpotifyEntity, boolean: Boolean)
}