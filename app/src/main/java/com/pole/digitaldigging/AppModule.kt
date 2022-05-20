package com.pole.digitaldigging

import com.pole.digitaldigging.screens.albumscreen.AlbumScreenViewModel
import com.pole.digitaldigging.screens.artistscreen.ArtistScreenViewModel
import com.pole.digitaldigging.screens.search.SearchViewModel
import com.pole.digitaldigging.screens.trackscreen.TrackScreenViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    viewModel {
        SearchViewModel(
            getSearchResults = get()
        )
    }

    viewModel {
        ArtistScreenViewModel(
            getArtist = get(),
            getArtistAlbums = get()
        )
    }

    viewModel {
        AlbumScreenViewModel(
            getAlbum = get(),
            getAlbumTracks = get(),
            getArtists = get(),
        )
    }

    viewModel {
        TrackScreenViewModel(
            getTrack = get(),
            getArtists = get(),
            getAlbum = get(),
            getSuggestedTracks = get()
        )
    }
}


