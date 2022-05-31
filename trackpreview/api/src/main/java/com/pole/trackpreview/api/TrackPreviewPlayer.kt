package com.pole.trackpreview.api

import androidx.lifecycle.LiveData

abstract class TrackPreviewPlayer : LiveData<TrackPreviewState>() {
    abstract fun setUrl(url: String?)
    abstract fun play()
    abstract fun pause()
}