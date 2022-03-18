package com.example.digitaldigging

import com.pole.data.RepositoryImpl
import com.pole.data.spotifyapi.SpotifyApiImpl
import com.pole.domain.Repository
import com.pole.domain.model.SpotifyApi
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule

@Module
@InstallIn(SingletonComponent::class)
interface AppBindings {

    @Binds
    fun bindRepositoryImpl(repositoryImpl: RepositoryImpl): Repository

    @Binds
    fun bindSpotifyApp(spotifyApiImpl: SpotifyApiImpl): SpotifyApi
}