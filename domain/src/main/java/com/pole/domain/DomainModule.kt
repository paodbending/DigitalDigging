package com.pole.domain

import com.pole.domain.usecases.*
import org.koin.dsl.module

val domainModule = module {

    single { GetAlbum(repository = get()) }

    single { GetAlbums(repository = get()) }

    single { GetAlbumTracks(repository = get()) }

    single { GetArtist(repository = get()) }

    single { GetArtistAlbums(repository = get()) }

    single { GetArtists(repository = get()) }

    single {
        GetSearchResults(
            repository = get(),
            getArtists = get(),
            getTracks = get(),
            getAlbums = get()
        )
    }

    single { GetSuggestedArtists(repository = get()) }

    single { GetSuggestedTracks(repository = get()) }

    single { GetTrack(repository = get()) }

    single { GetTracks(repository = get()) }
}
