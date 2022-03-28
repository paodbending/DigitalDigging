package com.pole.data.databases.spotifycache

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pole.data.databases.spotifycache.album.CachedAlbum
import com.pole.data.databases.spotifycache.artist.CachedArtist
import com.pole.data.databases.spotifycache.artistalbums.ArtistAlbumsRequest
import com.pole.data.databases.spotifycache.artistalbums.ArtistAlbumsResult
import com.pole.data.databases.spotifycache.album.AlbumDao
import com.pole.data.databases.spotifycache.albumtracks.AlbumTracksDao
import com.pole.data.databases.spotifycache.albumtracks.AlbumTracksRequest
import com.pole.data.databases.spotifycache.albumtracks.AlbumTracksResult
import com.pole.data.databases.spotifycache.artist.ArtistDao
import com.pole.data.databases.spotifycache.artistalbums.ArtistAlbumsDao
import com.pole.data.databases.spotifycache.search.SearchDao
import com.pole.data.databases.spotifycache.track.TrackDao
import com.pole.data.databases.spotifycache.search.CachedSearchResult
import com.pole.data.databases.spotifycache.search.SearchRequest
import com.pole.data.databases.spotifycache.suggestedartists.SuggestedArtistsRequest
import com.pole.data.databases.spotifycache.suggestedartists.SuggestedArtistsDao
import com.pole.data.databases.spotifycache.suggestedartists.SuggestedArtistsResult
import com.pole.data.databases.spotifycache.track.CachedTrack
import com.pole.data.databases.spotifycache.suggetedtracks.SuggestedTracksDao
import com.pole.data.databases.spotifycache.suggetedtracks.SuggestedTracksRequest
import com.pole.data.databases.spotifycache.suggetedtracks.SuggestedTracksResult

@Database(
    entities = [
        CachedArtist::class,
        CachedAlbum::class,
        CachedTrack::class,

        SearchRequest::class,
        CachedSearchResult::class,

        ArtistAlbumsRequest::class,
        ArtistAlbumsResult::class,

        AlbumTracksRequest::class,
        AlbumTracksResult::class,

        SuggestedTracksRequest::class,
        SuggestedTracksResult::class,

        SuggestedArtistsRequest::class,
        SuggestedArtistsResult::class,
    ],
    version = 1
)
internal abstract class SpotifyCacheDatabase : RoomDatabase() {
    abstract fun searchDao(): SearchDao
    abstract fun artistDao(): ArtistDao
    abstract fun albumDao(): AlbumDao
    abstract fun trackDao(): TrackDao
    abstract fun artistAlbumsDao(): ArtistAlbumsDao
    abstract fun albumTracksDao(): AlbumTracksDao
    abstract fun suggestedTracksDao(): SuggestedTracksDao
    abstract fun suggestedArtistsDao(): SuggestedArtistsDao
}