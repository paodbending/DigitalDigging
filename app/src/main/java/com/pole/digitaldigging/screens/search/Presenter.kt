package com.pole.digitaldigging.screens.search

interface Presenter {
    fun setSearchQuery(searchQuery: String)
    fun setSearchType(searchType: SearchType)

    fun setArtistSortType(artistSortType: ArtistSortType)
    fun setAlbumSortType(albumSortType: AlbumSortType)
    fun setTrackSortType(trackSortType: TrackSortType)
}