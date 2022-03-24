package com.pole.data

import android.content.Context
import androidx.room.Room
import com.adamratzman.spotify.SpotifyAppApiBuilder
import com.adamratzman.spotify.spotifyAppApi
import com.pole.data.database.Database
import com.pole.domain.Repository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideSpotifyApiBuilder(): SpotifyAppApiBuilder = spotifyAppApi(
        clientId = "dc57776c7b8d4c62875bfaa11e0bb70d",
        clientSecret = "9be837a031c54240941c373f0d0c55a1"
    )

    @Provides
    @Singleton
    internal fun provideDatabase(@ApplicationContext context: Context): Database =
        Room.databaseBuilder(
            context,
            Database::class.java, "database.db"
        ).build()
}

@Module
@InstallIn(SingletonComponent::class)
internal interface DataBindings {
    @Binds
    fun bindRepositoryImpl(repositoryImpl: RepositoryImpl): Repository
}