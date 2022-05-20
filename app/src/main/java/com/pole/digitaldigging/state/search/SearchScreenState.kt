package com.pole.digitaldigging.state.search

import com.pole.digitaldigging.UIResource
import com.pole.digitaldigging.state.search.intent.*
import com.pole.domain.entities.Album
import com.pole.domain.entities.Artist
import com.pole.domain.entities.Track


sealed interface SearchScreenState : SetSearchQueryIntent, SetSearchTypeIntent {

    val searchQuery: String

    data class BestResults(
        override val searchQuery: String,
        val results: UIResource<Results>,
        private val setSearchQueryDelegate: SetSearchQueryIntent,
        private val onArtistClickDelegate: OnArtistClickIntent,
        private val setSearchTypeDelegate: SetSearchTypeIntent,
    ) : SearchScreenState,
        SetSearchQueryIntent by setSearchQueryDelegate,
        OnArtistClickIntent by onArtistClickDelegate,
        SetSearchTypeIntent by setSearchTypeDelegate {
        data class Results(
            val artist: Artist?,
            val albums: List<Album>,
            val tracks: List<Track>,
        )
    }

    data class Artists(
        override val searchQuery: String,
        val sortBy: SortBy,
        val artists: UIResource<List<Artist>>,
        private val setSearchQueryDelegate: SetSearchQueryIntent,
        private val onArtistClickDelegate: OnArtistClickIntent,
        private val setSearchTypeDelegate: SetSearchTypeIntent,
        private val setArtistSortByDelegate: SetArtistSortByIntent,
    ) : SearchScreenState,
        SetSearchQueryIntent by setSearchQueryDelegate,
        OnArtistClickIntent by onArtistClickDelegate,
        SetSearchTypeIntent by setSearchTypeDelegate,
        SetArtistSortByIntent by setArtistSortByDelegate {
        enum class SortBy {
            RELEVANCE, POPULARITY, FOLLOWERS
        }
    }

    data class Albums(
        override val searchQuery: String,
        val sortBy: SortBy,
        val albums: UIResource<List<Album>>,
        private val setSearchQueryDelegate: SetSearchQueryIntent,
        private val setSearchTypeDelegate: SetSearchTypeIntent,
        private val setAlbumSortByDelegate: SetAlbumSortByIntent,
    ) : SearchScreenState,
        SetSearchQueryIntent by setSearchQueryDelegate,
        SetSearchTypeIntent by setSearchTypeDelegate,
        SetAlbumSortByIntent by setAlbumSortByDelegate {
        enum class SortBy {
            RELEVANCE, POPULARITY, RELEASE_DATE
        }
    }

    data class Tracks(
        override val searchQuery: String,
        val sortBy: SortBy,
        val tracks: UIResource<List<Track>>,
        private val setSearchQueryDelegate: SetSearchQueryIntent,
        private val setSearchTypeDelegate: SetSearchTypeIntent,
        private val setTrackSortByDelegate: SetTrackSortByIntent,
    ) : SearchScreenState,
        SetSearchQueryIntent by setSearchQueryDelegate,
        SetSearchTypeIntent by setSearchTypeDelegate,
        SetTrackSortByIntent by setTrackSortByDelegate {
        enum class SortBy {
            RELEVANCE, POPULARITY, LENGTH
        }
    }
}