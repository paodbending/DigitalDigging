package com.pole.trackpreview.api

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TrackPreviewViewModel @Inject constructor(
    val trackPreviewPlayer: TrackPreviewPlayer
) : ViewModel()