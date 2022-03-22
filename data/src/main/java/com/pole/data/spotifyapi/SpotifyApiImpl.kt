package com.pole.data.spotifyapi

import com.adamratzman.spotify.SpotifyAppApi
import com.adamratzman.spotify.SpotifyAppApiBuilder
import com.pole.data.*
import com.pole.data.toAlbum
import com.pole.data.toAlbumInfo
import com.pole.data.toArtistInfo
import com.pole.data.toTrack
import com.pole.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpotifyApiImpl @Inject constructor(
    private val spotifyAppApiBuilder: SpotifyAppApiBuilder
) : SpotifyApi {

    private var spotifyApi: SpotifyAppApi? = null

    override val isOnline: Boolean
        get() = spotifyApi != null

    override suspend fun setup() {
        spotifyApi = try {
            spotifyAppApiBuilder.build()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun searchArtist(query: String): List<ArtistInfo> {
        return spotifyApi?.let { api ->
            api.search.searchArtist(query).filterNotNull()
                .map { it.toArtistInfo() }
        } ?: emptyList()
    }

    override suspend fun getArtistInfo(spotifyId: String): ArtistInfo? {
        return spotifyApi?.let { api -> api.artists.getArtist(spotifyId)?.toArtistInfo() }
    }

    override suspend fun getArtistAlbums(spotifyId: String): List<Album> {
        return spotifyApi?.let { api ->
            api.artists.getArtistAlbums(spotifyId).filterNotNull().map { it.toAlbum() }
        } ?: emptyList()
    }

    override suspend fun getRelatedArtistsInfo(spotifyId: String): List<ArtistInfo> {
        return spotifyApi?.let { api ->
            api.artists.getRelatedArtists(spotifyId).filterNotNull()
                .map { it.toArtistInfo() }
        } ?: emptyList()
    }


    override suspend fun getAlbumInfo(spotifyId: String): AlbumInfo? {

        return spotifyApi?.let { api -> api.albums.getAlbum(spotifyId)?.toAlbumInfo() }
    }

    override suspend fun getAlbumTracks(spotifyId: String): List<Track> {
        return spotifyApi?.let { api ->
            api.albums.getAlbumTracks(spotifyId).filterNotNull().map { it.toTrack() }
        } ?: emptyList()
    }

    override suspend fun getTrackInfo(spotifyId: String): TrackInfo? {
        return spotifyApi?.tracks?.getTrack(spotifyId)?.toTrackInfo()
    }
}