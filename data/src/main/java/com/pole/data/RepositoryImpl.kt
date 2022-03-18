package com.pole.data

import com.pole.domain.Repository
import com.pole.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepositoryImpl @Inject constructor(
    private val spotifyApi: SpotifyApi
) : Repository {

    override suspend fun searchArtist(query: String): List<ArtistInfo> {
        return spotifyApi.searchArtist(query)
    }

    override suspend fun getArtistInfo(spotifyId: String): ArtistInfo? {
        return spotifyApi.getArtistInfo(spotifyId)
    }

    override suspend fun getArtistAlbums(spotifyId: String): List<Album> {
        return spotifyApi.getArtistAlbums(spotifyId)
    }

    override suspend fun getRelatedArtistsInfo(spotifyId: String): List<ArtistInfo> {
        return spotifyApi.getRelatedArtistsInfo(spotifyId)
    }


    override suspend fun getAlbumInfo(spotifyId: String): AlbumInfo? {
        return spotifyApi.getAlbumInfo(spotifyId)
    }

    override suspend fun getAlbumTracks(spotifyId: String): List<Track> {
        return spotifyApi.getAlbumTracks(spotifyId)
    }
}