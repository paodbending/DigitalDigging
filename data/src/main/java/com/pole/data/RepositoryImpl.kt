package com.pole.data

import com.adamratzman.spotify.SpotifyAppApi
import com.adamratzman.spotify.SpotifyAppApiBuilder
import com.adamratzman.spotify.endpoints.pub.ArtistApi
import com.adamratzman.spotify.endpoints.pub.SearchApi
import com.pole.data.databases.spotifycache.*
import com.pole.data.databases.spotifycache.albumtracks.AlbumTracksResult
import com.pole.data.databases.spotifycache.artistalbums.ArtistAlbumsResult
import com.pole.data.databases.spotifycache.search.CachedSearchResult
import com.pole.data.databases.spotifycache.suggestedartists.SuggestedArtistsResult
import com.pole.data.databases.spotifycache.suggetedtracks.SuggestedTracksResult
import com.pole.domain.AppError
import com.pole.domain.Repository
import com.pole.domain.entities.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class RepositoryImpl(
    private val spotifyAppApiBuilder: SpotifyAppApiBuilder,
    spotifyCacheDatabase: SpotifyCacheDatabase,
) : Repository {

    private val searchDao = spotifyCacheDatabase.searchDao()
    private val artistDao = spotifyCacheDatabase.artistDao()
    private val albumDao = spotifyCacheDatabase.albumDao()
    private val trackDao = spotifyCacheDatabase.trackDao()
    private val artistAlbumsDao = spotifyCacheDatabase.artistAlbumsDao()
    private val albumTracksDao = spotifyCacheDatabase.albumTracksDao()
    private val suggestedTracksDao = spotifyCacheDatabase.suggestedTracksDao()
    private val suggestedArtistsDao = spotifyCacheDatabase.suggestedArtistsDao()

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
        block: (SpotifyAppApi) -> Unit,
    ) {
        getSpotifyApi().let { api ->
            if (api == null) {
                emit(NetworkResource.Error(AppError.NetworkError))
            } else {
                try {
                    block(api)
                } catch (e: Exception) {
                    emit(NetworkResource.Error(AppError.NetworkError))
                }
            }
        }
    }

    override fun getSearchResultIds(searchQuery: String): Flow<NetworkResource<SearchResultIds>> =
        flow {
            emit(NetworkResource.Loading())
            searchDao.getRequest(searchQuery).collect { request ->
                if (request == null || request.isNotValid) {
                    safeApiCall { api ->
                        val results = api.search.search(
                            searchQuery, searchTypes = arrayOf(
                                SearchApi.SearchType.ARTIST,
                                SearchApi.SearchType.ALBUM,
                                SearchApi.SearchType.TRACK
                            )
                        )

                        // Update cache since we're here
                        results.artists?.mapNotNull { it?.toCachedArtist() }?.let {
                            artistDao.upsertArtists(it)
                        }
                        results.tracks?.mapNotNull { it?.toCachedTrack() }?.let {
                            trackDao.upsertCachedTracks(it)
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
                } else {
                    searchDao.getResults(searchQuery).collect { results ->
                        emit(
                            NetworkResource.Ready(
                                SearchResultIds(
                                    artistsIds = results.filter { it.type == SpotifyType.ARTIST.toString() }
                                        .map { it.id },
                                    albumsIds = results.filter { it.type == SpotifyType.ALBUM.toString() }
                                        .map { it.id },
                                    tracksIds = results.filter { it.type == SpotifyType.TRACK.toString() }
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
        trackDao.getCachedTrack(id).collect { cachedTrack ->
            if (cachedTrack == null || cachedTrack.isNotValid) {
                safeApiCall { api ->
                    api.tracks.getTrack(id)?.let {
                        trackDao.upsertCachedTrack(it.toCachedTrack())
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
            .getCachedTracks(ids.toList())
            .map { cachedTrack ->
                cachedTrack.filterNot { it.isNotValid }
            }
            .collect { cachedTracks ->
                if (cachedTracks.size == ids.size) {
                    emit(NetworkResource.Ready(cachedTracks.map { it.toModelTrack() }))
                } else {
                    // Some elements are missing in cache
                    val cachedSet = cachedTracks.map { it.id }.toSet()
                    val missingIds = ids.filterNot { cachedSet.contains(it) }.toTypedArray()
                    // Cache missing artists
                    safeApiCall { api ->
                        api.tracks.getTracks(*missingIds).mapNotNull { it }.let {
                            trackDao.upsertCachedTracks(it.map { track -> track.toCachedTrack() })
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
                        safeApiCall { api ->
                            api.browse.getRecommendations(seedTracks = listOf(seedTrackId)).tracks
                                .let { results ->
                                    // Update cache since we're here
                                    trackDao.upsertCachedTracks(results.map { it.toCachedTrack() })

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
                        suggestedTracksDao.getResults(seedTrackId).collect { results ->
                            getTracks(results.map { it.suggestedTrackId }.toSet())
                                .collect { emit(it) }
                        }
                    }
                }
        }
}
