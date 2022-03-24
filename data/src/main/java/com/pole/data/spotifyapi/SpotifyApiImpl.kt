package com.pole.data.spotifyapi

import com.adamratzman.spotify.SpotifyAppApi
import com.adamratzman.spotify.SpotifyAppApiBuilder
import com.pole.data.*
import com.pole.data.toAlbum
import com.pole.data.toAlbumInfo
import com.pole.data.toArtistInfo
import com.pole.data.toTrack
import com.pole.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    override suspend fun searchArtist(query: String): List<ArtistInfo> =
        withContext(Dispatchers.IO) {
            spotifyApi?.let { api ->
                api.search.searchArtist(query)
                    .mapNotNull { it?.toArtistInfo() }
            } ?: emptyList()
        }

    override suspend fun getArtistInfo(spotifyId: String): ArtistInfo? =
        withContext(Dispatchers.IO) {
            spotifyApi?.let { api -> api.artists.getArtist(spotifyId)?.toArtistInfo() }
        }

    override suspend fun getArtistAlbums(spotifyId: String): List<Album> =
        withContext(Dispatchers.IO) {
            spotifyApi?.let { api ->
                api.artists.getArtistAlbums(spotifyId).mapNotNull { it?.toAlbum() }
            } ?: emptyList()
        }

    override suspend fun getRelatedArtistsInfo(spotifyId: String): List<ArtistInfo> =
        withContext(Dispatchers.IO) {
            spotifyApi?.let { api ->
                api.artists.getRelatedArtists(spotifyId)
                    .map { it.toArtistInfo() }
            } ?: emptyList()
        }

    override suspend fun getAlbumInfo(spotifyId: String): AlbumInfo? = withContext(Dispatchers.IO) {
        spotifyApi?.let { api -> api.albums.getAlbum(spotifyId)?.toAlbumInfo() }
    }

    override suspend fun getAlbumTracks(spotifyId: String): List<Track> =
        withContext(Dispatchers.IO) {
            spotifyApi?.let { api ->
                api.albums.getAlbumTracks(spotifyId).mapNotNull { it?.toTrack() }
            } ?: emptyList()
        }

    override suspend fun getTrackInfo(spotifyId: String): TrackInfo? = withContext(Dispatchers.IO) {
        spotifyApi?.tracks?.getTrack(spotifyId)?.toTrackInfo()
    }
}