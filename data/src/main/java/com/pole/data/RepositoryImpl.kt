package com.pole.data

import android.util.Log
import com.adamratzman.spotify.SpotifyAppApi
import com.adamratzman.spotify.SpotifyAppApiBuilder
import com.adamratzman.spotify.endpoints.pub.ArtistApi
import com.adamratzman.spotify.endpoints.pub.SearchApi
import com.pole.data.databases.spotifycache.*
import com.pole.data.databases.spotifycache.SpotifyCacheDatabase
import com.pole.data.databases.spotifycache.albumtracks.AlbumTracksResult
import com.pole.data.databases.spotifycache.artistalbums.ArtistAlbumsResult
import com.pole.data.databases.spotifycache.search.CachedSearchResult
import com.pole.data.databases.spotifycache.suggestedartists.SuggestedArtistsResult
import com.pole.data.databases.spotifycache.suggetedtracks.SuggestedTracksResult
import com.pole.data.databases.spotifycache.toModelAlbum
import com.pole.data.databases.spotifycache.toModelArtist
import com.pole.data.databases.spotifycache.toModelTrack
import com.pole.data.databases.userdata.DatabaseUserData
import com.pole.data.databases.userdata.UserDataDatabase
import com.pole.domain.Repository
import com.pole.domain.model.*
import com.pole.domain.model.spotify.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class RepositoryImpl @Inject constructor(
    private val spotifyAppApiBuilder: SpotifyAppApiBuilder,
    spotifyCacheDatabase: SpotifyCacheDatabase,
    userDataDatabase: UserDataDatabase
) : Repository {

    private val searchDao = spotifyCacheDatabase.searchDao()
    private val artistDao = spotifyCacheDatabase.artistDao()
    private val albumDao = spotifyCacheDatabase.albumDao()
    private val trackDao = spotifyCacheDatabase.trackDao()
    private val artistAlbumsDao = spotifyCacheDatabase.artistAlbumsDao()
    private val albumTracksDao = spotifyCacheDatabase.albumTracksDao()
    private val suggestedTracksDao = spotifyCacheDatabase.suggestedTracksDao()
    private val suggestedArtistsDao = spotifyCacheDatabase.suggestedArtistsDao()

    private val userDataDao = userDataDatabase.dao()

    private var _spotifyApi: SpotifyAppApi? = null
    private val _spotifyApiMutex = Mutex()
    private suspend fun getSpotifyApi(): SpotifyAppApi? {
        _spotifyApiMutex.withLock {
            if (_spotifyApi == null) {
                _spotifyApi = try {
                    spotifyAppApiBuilder.build()
                } catch (e: Exception) {
                    null
                }
                _spotifyApi?.spotifyApiOptions?.let {
                    it.requestTimeoutMillis = 5_000
                }
            }
        }
        return _spotifyApi
    }

    private suspend inline fun <T> FlowCollector<NetworkResource<T>>.safeApiCall(
        block: (SpotifyAppApi) -> Unit
    ) {
        getSpotifyApi().let { api ->
            if (api == null) {
                emit(NetworkResource.Error())
            } else {
                try {
                    block(api)
                } catch (e: Exception) {
                    emit(NetworkResource.Error())
                }
            }
        }
    }

    override fun getSearchResults(searchQuery: String): Flow<NetworkResource<SearchResult>> = flow {
        emit(NetworkResource.Loading())
        searchDao.getRequest(searchQuery).collect { request ->
            if (request == null || request.isNotValid) {
                safeApiCall { api ->
                    api.search.search(
                        searchQuery, searchTypes = arrayOf(
                            SearchApi.SearchType.ARTIST,
                            SearchApi.SearchType.ALBUM,
                            SearchApi.SearchType.TRACK
                        )
                    ).let { results ->

                        // Update cache since we're here
                        results.artists?.mapNotNull { it?.toCachedArtist() }?.let {
                            artistDao.upsertArtists(it)
                        }
                        results.tracks?.mapNotNull { it?.toCachedTrack() }?.let {
                            trackDao.upsertTracks(it)
                        }

                        searchDao.updateResults(
                            searchQuery = searchQuery,
                            results = (results.artists?.mapNotNull { it }
                                ?.mapIndexed { index, artist ->
                                    CachedSearchResult(
                                        searchQuery,
                                        artist.id,
                                        index,
                                        SpotifyType.ARTIST.toString()
                                    )
                                } ?: emptyList()) +
                                    (results.albums?.mapNotNull { it }
                                        ?.mapIndexed { index, album ->
                                            CachedSearchResult(
                                                searchQuery,
                                                album.id,
                                                index,
                                                SpotifyType.ALBUM.toString()
                                            )
                                        } ?: emptyList()) +
                                    (results.tracks?.mapNotNull { it }
                                        ?.mapIndexed { index, track ->
                                            CachedSearchResult(
                                                searchQuery,
                                                track.id,
                                                index,
                                                SpotifyType.TRACK.toString()
                                            )
                                        } ?: emptyList()),
                        )
                    }
                }
            } else {
                searchDao.getResults(searchQuery).collect { results ->
                    emit(
                        NetworkResource.Ready(
                            SearchResult(
                                artistIds = results.filter { it.type == SpotifyType.ARTIST.toString() }
                                    .map { it.id },
                                albumIds = results.filter { it.type == SpotifyType.ALBUM.toString() }
                                    .map { it.id },
                                trackIds = results.filter { it.type == SpotifyType.TRACK.toString() }
                                    .map { it.id },
                            )
                        )
                    )
                }
            }
        }
    }

    override fun getArtist(id: String): Flow<NetworkResource<Artist>> = flow {
        emit(NetworkResource.Loading())
        artistDao.getArtist(id).collect { cachedArtist ->
            if (cachedArtist == null || cachedArtist.isNotValid) {
                safeApiCall { api ->
                    api.artists.getArtist(id)?.let {
                        artistDao.upsertArtist(it.toCachedArtist())
                    }
                }
            } else {
                emit(NetworkResource.Ready(cachedArtist.toModelArtist()))
            }
        }
    }

    override fun getArtists(ids: Set<String>): Flow<NetworkResource<List<Artist>>> = flow {
        emit(NetworkResource.Loading())
        artistDao
            .getArtists(ids.toList())
            .map { artists -> artists.filterNot { it.isNotValid } }
            .collect { artists ->
                if (artists.size == ids.size) {
                    emit(NetworkResource.Ready(artists.map { it.toModelArtist() }))
                } else {
                    // Some elements are missing in cache
                    val cachedSet = artists.map { it.id }.toSet()
                    val missingIds = ids.filterNot { cachedSet.contains(it) }.toTypedArray()
                    // Cache missing artists
                    safeApiCall { api ->
                        api.artists.getArtists(*missingIds).mapNotNull { it }.let {
                            artistDao.upsertArtists(it.map { artist -> artist.toCachedArtist() })
                        }
                    }
                }
            }
    }

    override fun getArtistAlbums(artistId: String): Flow<NetworkResource<List<Album>>> = flow {
        emit(NetworkResource.Loading())
        artistAlbumsDao
            .getRequest(artistId)
            .collect { request ->
                if (request == null || request.isNotValid) {
                    safeApiCall { api ->
                        api.artists.getArtistAlbums(
                            artistId,
                            include = arrayOf(
                                ArtistApi.AlbumInclusionStrategy.ALBUM,
                                ArtistApi.AlbumInclusionStrategy.SINGLE,
                            )
                        ).let { results ->
                            artistAlbumsDao.updateResults(
                                artistId = artistId,
                                results = results.mapNotNull { it }.map {
                                    ArtistAlbumsResult(
                                        artistId,
                                        it.id
                                    )
                                }
                            )
                        }
                    }
                } else {
                    artistAlbumsDao.getResults(artistId).collect { results ->
                        getAlbums(results.map { it.albumId }.toSet()).collect { emit(it) }
                    }
                }
            }
    }

    override fun getAlbum(id: String): Flow<NetworkResource<Album>> = flow {
        emit(NetworkResource.Loading())
        albumDao.getAlbum(id).collect { album ->
            if (album == null || album.isNotValid) {
                safeApiCall { api ->
                    api.albums.getAlbum(id)?.let {
                        albumDao.upsertAlbum(it.toCachedAlbum())
                    }
                }
            } else {
                emit(NetworkResource.Ready(album.toModelAlbum()))
            }
        }
    }

    override fun getAlbums(ids: Set<String>): Flow<NetworkResource<List<Album>>> = flow {
        emit(NetworkResource.Loading())
        albumDao
            .getAlbums(ids.toList())
            .map { albums -> albums.filterNot { it.isNotValid } }
            .collect { albums ->

                if (albums.size == ids.size) {
                    emit(NetworkResource.Ready(albums.map { it.toModelAlbum() }))
                } else {
                    // Some elements are missing in cache
                    val cachedSet = albums.map { it.id }.toSet()
                    val missingIds = ids.filterNot { cachedSet.contains(it) }.toTypedArray()
                    // Cache missing artists
                    safeApiCall { api ->
                        api.albums.getAlbums(*missingIds).mapNotNull { it }.let {
                            albumDao.upsertAlbums(it.map { album -> album.toCachedAlbum() })
                        }
                    }
                }
            }
    }

    override fun getAlbumTracks(albumId: String): Flow<NetworkResource<List<Track>>> = flow {
        emit(NetworkResource.Loading())
        albumTracksDao
            .getRequest(albumId)
            .collect { request ->
                if (request == null || request.isNotValid) {
                    safeApiCall { api ->
                        api.albums.getAlbumTracks(albumId).let { results ->

                            // Update cache since we're here
                            trackDao.upsertTracks(results.mapNotNull { it?.toCachedTrack(albumId) })

                            albumTracksDao.updateResults(
                                albumId = albumId,
                                results = results.mapNotNull { it }.map {
                                    AlbumTracksResult(
                                        albumId,
                                        it.id
                                    )
                                }
                            )
                        }
                    }
                } else {
                    albumTracksDao.getResults(albumId).collect { results ->
                        getTracks(results.map { it.trackId }.toSet()).collect { emit(it) }
                    }
                }
            }
    }

    override fun getTrack(id: String): Flow<NetworkResource<Track>> = flow {
        emit(NetworkResource.Loading())
        trackDao.getTrack(id).collect { cachedTrack ->
            if (cachedTrack == null || cachedTrack.isNotValid) {
                safeApiCall { api ->
                    api.tracks.getTrack(id)?.let {
                        trackDao.upsertTrack(it.toCachedTrack())
                    }
                }
            } else {
                emit(NetworkResource.Ready(cachedTrack.toModelTrack()))
            }
        }
    }

    override fun getTracks(ids: Set<String>): Flow<NetworkResource<List<Track>>> = flow {
        emit(NetworkResource.Loading())
        trackDao
            .getTracks(ids.toList())
            .map { tracks -> tracks.filterNot { it.isNotValid } }
            .collect { tracks ->
                if (tracks.size == ids.size) {
                    Log.v("Poro", "Repo.getTracks: Emitting ${tracks.size} tracks")
                    emit(NetworkResource.Ready(tracks.map { it.toModelTrack() }))
                } else {
                    // Some elements are missing in cache
                    val cachedSet = tracks.map { it.id }.toSet()
                    val missingIds = ids.filterNot { cachedSet.contains(it) }.toTypedArray()
                    // Cache missing artists
                    safeApiCall { api ->
                        Log.v("Poro", "Repo.getTracks: Downlading ${missingIds.size} tracks")
                        api.tracks.getTracks(*missingIds).mapNotNull { it }.let {
                            trackDao.upsertTracks(it.map { track -> track.toCachedTrack() })
                        }
                    }
                }
            }
    }

    override fun getSuggestedArtists(seedArtistId: String): Flow<NetworkResource<List<Artist>>> =
        flow {
            emit(NetworkResource.Loading())
            suggestedArtistsDao
                .getRequest(seedArtistId)
                .collect { request ->
                    if (request == null || request.isNotValid) {
                        safeApiCall { api ->
                            val results = api.artists.getRelatedArtists(seedArtistId)
                            // Update cache since we're here
                            artistDao.upsertArtists(results.map { it.toCachedArtist() })
                            suggestedArtistsDao.updateResults(
                                seedArtistId = seedArtistId,
                                results = results.mapIndexed { i, artist ->
                                    SuggestedArtistsResult(
                                        seedArtistId = seedArtistId,
                                        suggestedArtistId = artist.id,
                                        resultOrder = i
                                    )
                                }
                            )
                        }
                    } else {
                        suggestedArtistsDao.getResults(seedArtistId).collect { results ->
                            emitAll(getArtists(results.map { it.suggestedArtistId }.toSet()))
                        }
                    }
                }
        }

    override fun getSuggestedTracks(seedTrackId: String): Flow<NetworkResource<List<Track>>> =
        flow {
            emit(NetworkResource.Loading())
            suggestedTracksDao
                .getRequest(seedTrackId)
                .collect { request ->
                    if (request == null || request.isNotValid) {
                        Log.v("Poro", "Repo.getSuggestTrack: Cached request is not valid")
                        safeApiCall { api ->
                            api.browse.getRecommendations(seedTracks = listOf(seedTrackId)).tracks
                                .let { results ->
                                    // Update cache since we're here
                                    trackDao.upsertTracks(results.map { it.toCachedTrack() })

                                    Log.v(
                                        "Poro",
                                        "Repo.getSuggestTrack: api found ${results.size} tracks"
                                    )

                                    suggestedTracksDao.updateResults(
                                        seedTrackId = seedTrackId,
                                        results = results.mapIndexed { i, track ->
                                            SuggestedTracksResult(
                                                seedTrackId = seedTrackId,
                                                suggestedTrackId = track.id,
                                                resultOrder = i
                                            )
                                        }
                                    )
                                }
                        }
                    } else {
                        Log.v("Poro", "Repo.getSuggestTrack: Cached request is valid")
                        suggestedTracksDao.getResults(seedTrackId).collect { results ->
                            Log.v("Poro", "Repo.getSuggestTrack: emitting ${results.size}")
                            getTracks(results.map { it.suggestedTrackId }.toSet())
                                .collect { emit(it) }
                        }
                    }
                }
        }

    override fun getUserData(id: String, spotifyType: SpotifyType): Flow<UserData> {
        return userDataDao.getUserData(id).map {
            UserData(
                dateAddedToSchedule = it?.dateAddedToSchedule,
                dateAddedToLibrary = it?.dateAddedToLibrary
            )
        }
    }

    override suspend fun flipLibrary(id: String, type: SpotifyType) {
        val databaseValue = getUserData(id, type).first()
        userDataDao.upsertUserData(
            DatabaseUserData(
                id = id,
                type = type.toString(),
                dateAddedToLibrary = if (databaseValue.library) null else System.currentTimeMillis(),
                dateAddedToSchedule = databaseValue.dateAddedToSchedule
            )
        )
    }

    override suspend fun flipScheduled(id: String, type: SpotifyType) {
        val databaseValue = getUserData(id, type).first()
        userDataDao.upsertUserData(
            DatabaseUserData(
                id = id,
                type = type.toString(),
                dateAddedToLibrary = databaseValue.dateAddedToLibrary,
                dateAddedToSchedule = if (databaseValue.scheduled) null else System.currentTimeMillis(),
            )
        )
    }

    override suspend fun upsertUserData(
        id: String,
        spotifyType: SpotifyType,
        userData: UserData
    ) {
        return userDataDao.upsertUserData(
            DatabaseUserData(
                id = id,
                type = spotifyType.toString(),
                dateAddedToLibrary = userData.dateAddedToLibrary,
                dateAddedToSchedule = userData.dateAddedToSchedule
            )
        )
    }
}
