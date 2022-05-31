package com.pole.trackpreview.di

import com.pole.trackpreview.api.TrackPreviewPlayer
import com.pole.trackpreview.impl.TrackPreviewPlayerMediaPlayerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object TrackPreviewModule

@Module
@InstallIn(SingletonComponent::class)
internal interface TrackPreviewBindings {
    @Binds
    fun bindTrackPreviewImpl(trackPreviewPlayerMediaPlayerImpl: TrackPreviewPlayerMediaPlayerImpl): TrackPreviewPlayer
}