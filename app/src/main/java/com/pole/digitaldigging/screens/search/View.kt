package com.pole.digitaldigging.screens.search

import com.pole.domain.entities.Album
import com.pole.domain.entities.Artist
import com.pole.domain.entities.Track

interface View {
    fun setSearchQuery(searchQuery: String)

    fun setSearchType(searchType: SearchType)
    fun setArtistSortType(artistSortType: ArtistSortType)
    fun setAlbumSortType(albumSortType: AlbumSortType)
    fun setTrackSortType(trackSortType: TrackSortType)

    fun setBestResults(artist: Artist?, albums: List<Album>, tracks: List<Track>)
    fun setArtistsResults(artists: List<Artist>)
    fun setAlbumsResults(albums: List<Album>)
    fun setTracksResults(tracks: List<Track>)

    fun showSearchMessage()
    fun showErrorMessage()
    fun showProgressBar()

    fun showBestResults()
    fun showArtistsResults()
    fun showAlbumsResults()
    fun showTracksResults()
}