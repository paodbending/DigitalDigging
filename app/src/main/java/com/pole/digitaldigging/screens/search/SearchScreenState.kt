package com.pole.digitaldigging.screens.search

import com.pole.domain.entities.Album
import com.pole.domain.entities.Artist
import com.pole.domain.entities.Track

enum class SearchType {
    ALL, ARTISTS, ALBUMS, TRACKS
}

enum class ArtistSortType {
    RELEVANCE, POPULARITY, FOLLOWERS
}

enum class AlbumSortType {
    RELEVANCE, POPULARITY, RELEASE_DATE
}

enum class TrackSortType {
    RELEVANCE, POPULARITY, LENGTH
}

data class SearchSettings(
    val searchType: SearchType,
    val artistSortType: ArtistSortType = ArtistSortType.RELEVANCE,
    val albumSortType: AlbumSortType = AlbumSortType.RELEVANCE,
    val trackSortType: TrackSortType = TrackSortType.RELEVANCE,
)

data class Results(
    val artists: List<Artist>,
    val albums: List<Album>,
    val tracks: List<Track>,
)