package com.pole.domain

import com.pole.domain.model.*
import com.pole.domain.model.spotify.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
interface Repository {

    // Search
    fun getSearchResults(searchQuery: String): Flow<NetworkResource<SearchResult>>

    // Artist
    fun getArtist(id: String): Flow<NetworkResource<Artist>>
    fun getArtists(ids: Set<String>): Flow<NetworkResource<List<Artist>>>
    fun getArtistAlbums(artistId: String): Flow<NetworkResource<List<Album>>>

    // Album
    fun getAlbum(id: String): Flow<NetworkResource<Album>>
    fun getAlbums(ids: Set<String>): Flow<NetworkResource<List<Album>>>
    fun getAlbumTracks(albumId: String): Flow<NetworkResource<List<Track>>>

    // Track
    fun getTrack(id: String): Flow<NetworkResource<Track>>
    fun getTracks(ids: Set<String>): Flow<NetworkResource<List<Track>>>

    // Recommendations
    fun getSuggestedArtists(seedArtistId: String): Flow<NetworkResource<List<Artist>>>
    fun getSuggestedTracks(seedTrackId: String): Flow<NetworkResource<List<Track>>>

    // User Data
    fun getUserData(id: String, spotifyType: SpotifyType): Flow<UserData>
    suspend fun flipLibrary(id: String, type: SpotifyType)
    suspend fun flipScheduled(id: String, type: SpotifyType)
    suspend fun upsertUserData(id: String, spotifyType: SpotifyType, userData: UserData)
}