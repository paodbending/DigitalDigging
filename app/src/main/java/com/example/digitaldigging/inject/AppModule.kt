package com.example.digitaldigging.inject

import com.pole.data.RepositoryImpl
import com.pole.domain.Repository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    suspend fun setup() {}
}

@Module
@InstallIn(SingletonComponent::class)
interface AppBindings {

    @Binds
    fun bindRepositoryImpl(repositoryImpl: RepositoryImpl): Repository
}