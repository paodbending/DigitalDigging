package com.pole.data

import com.adamratzman.spotify.SpotifyAppApi
import com.adamratzman.spotify.SpotifyAppApiBuilder
import com.pole.data.database.Database
import com.pole.data.database.UserData
import com.pole.domain.Repository
import com.pole.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class RepositoryImpl @Inject constructor(
    private val spotifyAppApiBuilder: SpotifyAppApiBuilder,
    database: Database
) : Repository {

    private val dao = database.dao()

    private var _spotifyApi: SpotifyAppApi? = null

    private val spotifyApiMutex = Mutex()

    private suspend fun getSpotifyApi(): SpotifyAppApi? {
        spotifyApiMutex.withLock {
            if (_spotifyApi == null) {
                _spotifyApi = try {
                    spotifyAppApiBuilder.build()
                } catch (e: Exception) {
                    null
                }
            }
        }
        return _spotifyApi
    }

    override suspend fun setup() {
        getSpotifyApi()
    }

    override suspend fun searchArtist(query: String): List<ArtistInfo> =
        withContext(Dispatchers.IO) {
            getSpotifyApi()?.search?.searchArtist(query)?.mapNotNull {
                it?.toArtistInfo()
            } ?: emptyList()
        }

    override suspend fun getArtistInfo(id: String): ArtistInfo? = withContext(Dispatchers.IO) {
        getSpotifyApi()?.artists?.getArtist(id)?.toArtistInfo()
    }

    override suspend fun getArtistAlbums(id: String): List<Album> =
        withContext(Dispatchers.IO) {
            _spotifyApi?.let { api ->
                api.artists.getArtistAlbums(id).mapNotNull { it?.toAlbum() }
            } ?: emptyList()
        }

    override suspend fun getRelatedArtistsInfo(id: String): List<ArtistInfo> =
        withContext(Dispatchers.IO) {
            _spotifyApi?.let { api ->
                api.artists.getRelatedArtists(id)
                    .map { it.toArtistInfo() }
            } ?: emptyList()
        }

    override suspend fun getAlbumInfo(id: String): AlbumInfo? =
        withContext(Dispatchers.IO) {
            _spotifyApi?.let { api -> api.albums.getAlbum(id)?.toAlbumInfo() }
        }

    override suspend fun getAlbumTracks(id: String): List<Track> =
        withContext(Dispatchers.IO) {
            _spotifyApi?.let { api ->
                api.albums.getAlbumTracks(id).mapNotNull { it?.toTrack() }
            } ?: emptyList()
        }

    override suspend fun getTrackInfo(id: String): TrackInfo? =
        withContext(Dispatchers.IO) {
            _spotifyApi?.tracks?.getTrack(id)?.toTrackInfo()
        }

    override suspend fun getUserData(
        entity: SpotifyEntity
    ): SpotifyEntityUserData {
        return dao.getUserDataFlow(entity.id).let {
            SpotifyEntityUserData(
                id = entity.id,
                type = entity.entityType,
                dateAddedToLibrary = it?.dateAddedToLibrary,
                dateAddedToSchedule = it?.dateAddedToSchedule
            )
        }
    }

    override suspend fun setLibrary(entity: SpotifyEntity, boolean: Boolean) {
        val userData = dao.getUserData(entity.id) ?: UserData(entity.id, entity.entityType)
        dao.updateUserData(
            userData.copy(
                dateAddedToLibrary = if (boolean) System.currentTimeMillis() else null
            )
        )
    }

    override suspend fun setScheduled(entity: SpotifyEntity, boolean: Boolean) {
        val userData = dao.getUserData(entity.id) ?: UserData(entity.id, entity.entityType)
        dao.updateUserData(
            userData.copy(
                dateAddedToSchedule = if (boolean) System.currentTimeMillis() else null
            )
        )
    }
}