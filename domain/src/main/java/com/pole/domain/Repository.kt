package com.pole.domain

import com.pole.domain.entities.*
import kotlinx.coroutines.flow.Flow

interface Repository {

    // Search
    fun getSearchResultIds(searchQuery: String): Flow<NetworkResource<SearchResultIds>>

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
}