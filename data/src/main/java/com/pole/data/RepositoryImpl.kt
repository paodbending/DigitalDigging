package com.pole.data

import com.adamratzman.spotify.SpotifyAppApi
import com.adamratzman.spotify.spotifyAppApi
import com.pole.domain.Repository
import com.pole.domain.model.Album
import com.pole.domain.model.ArtistInfo

class RepositoryImpl private constructor(
    private val spotifyApi: SpotifyAppApi
) : Repository {

    companion object {

        suspend fun create(): RepositoryImpl {

            val api = spotifyAppApi(
                clientId = "dc57776c7b8d4c62875bfaa11e0bb70d",
                clientSecret = "9be837a031c54240941c373f0d0c55a1"
            ).build()

            return RepositoryImpl(api)
        }
    }

    override suspend fun searchArtist(query: String): List<ArtistInfo> {
        return spotifyApi.search.searchArtist(query).filterNotNull().map { it.toArtistInfo() }
    }

    override suspend fun getArtist(spotifyId: String): ArtistInfo? {
        return spotifyApi.artists.getArtist(spotifyId)?.toArtistInfo()
    }

    override suspend fun getArtistAlbums(spotifyId: String): List<Album> {
        return spotifyApi.artists.getArtistAlbums(spotifyId).filterNotNull().map { it.toAlbum() }
    }

    override suspend fun getRelatedArtists(spotifyId: String): List<ArtistInfo> {
        return spotifyApi.artists.getRelatedArtists(spotifyId).filterNotNull()
            .map { it.toArtistInfo() }
    }
}