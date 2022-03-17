package com.pole.data

import com.adamratzman.spotify.SpotifyAppApi
import com.pole.domain.Repository
import com.pole.domain.model.Album
import com.pole.domain.model.ArtistInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepositoryImpl @Inject constructor(
    private val spotifyApi: SpotifyAppApi
) : Repository {

    override suspend fun searchArtist(query: String): List<ArtistInfo> {
        return spotifyApi.search.searchArtist(query).filterNotNull().map { it.toArtistInfo() }
    }

    override suspend fun getArtistInfo(spotifyId: String): ArtistInfo? {
        return spotifyApi.artists.getArtist(spotifyId)?.toArtistInfo()
    }

    override suspend fun getArtistAlbums(spotifyId: String): List<Album> {
        return spotifyApi.artists.getArtistAlbums(spotifyId).filterNotNull().map { it.toAlbum() }
    }

    override suspend fun getRelatedArtistsInfo(spotifyId: String): List<ArtistInfo> {
        return spotifyApi.artists.getRelatedArtists(spotifyId).filterNotNull()
            .map { it.toArtistInfo() }
    }
}