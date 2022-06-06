package com.pole.domain

import com.pole.domain.usecases.GetSearchResults
import com.pole.domain.usecases.GetSearchResultsImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DomainModule


@Module
@InstallIn(SingletonComponent::class)
internal interface DomainBindings {
    @Binds
    fun bindGetSearchResults(getSearchResultsImpl: GetSearchResultsImpl): GetSearchResults
}