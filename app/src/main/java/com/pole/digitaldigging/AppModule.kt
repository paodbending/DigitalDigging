package com.pole.digitaldigging

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule

@Module
@InstallIn(SingletonComponent::class)
interface AppBindings {

}