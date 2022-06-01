package com.pole.digitaldigging

import com.pole.digitaldigging.screens.search.*
import com.pole.domain.entities.Album
import com.pole.domain.entities.Artist
import com.pole.domain.entities.Track

open class MockView : View {

    override fun setSearchQuery(searchQuery: String) {}

    override fun setSearchType(searchType: SearchType) {}

    override fun setArtistSortType(artistSortType: ArtistSortType) {}

    override fun setAlbumSortType(albumSortType: AlbumSortType) {}

    override fun setTrackSortType(trackSortType: TrackSortType) {}

    override fun setBestResults(artist: Artist?, albums: List<Album>, tracks: List<Track>) {}

    override fun setArtistsResults(artists: List<Artist>) {}

    override fun setAlbumsResults(albums: List<Album>) {}

    override fun setTracksResults(tracks: List<Track>) {}

    override fun showSearchMessage() {}

    override fun showErrorMessage() {}

    override fun showProgressBar() {}

    override fun showBestResults() {}

    override fun showArtistsResults() {}

    override fun showAlbumsResults() {}

    override fun showTracksResults() {}
}