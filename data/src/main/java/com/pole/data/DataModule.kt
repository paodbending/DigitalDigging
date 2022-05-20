package com.pole.data

import androidx.room.Room
import com.adamratzman.spotify.spotifyAppApi
import com.pole.data.databases.spotifycache.SpotifyCacheDatabase
import com.pole.domain.Repository
import org.koin.dsl.module

val dataModule = module {
    single<Repository> {
        RepositoryImpl(
            spotifyAppApiBuilder = get(),
            spotifyCacheDatabase = get()
        )
    }

    single {
        spotifyAppApi(
            clientId = "dc57776c7b8d4c62875bfaa11e0bb70d",
            clientSecret = "9be837a031c54240941c373f0d0c55a1"
        )
    }

    single {
        Room.databaseBuilder(
            get(),
            SpotifyCacheDatabase::class.java, "spotify_cache_database.db"
        ).build()
    }
}