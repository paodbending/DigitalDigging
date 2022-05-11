package com.example.digitaldigging.screens.search

import com.example.digitaldigging.UIResource
import com.pole.domain.model.spotify.Album
import com.pole.domain.model.spotify.Artist
import com.pole.domain.model.spotify.Track

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

data class SearchScreenState(
    val searchQuery: String,
    val searchSettings: SearchSettings,
    val results: UIResource<Results>,
)