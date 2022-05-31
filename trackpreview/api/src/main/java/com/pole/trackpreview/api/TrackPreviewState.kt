package com.pole.trackpreview.api

sealed interface TrackPreviewState {

    object NotAvailable : TrackPreviewState

    object Loading : TrackPreviewState

    object Ready : TrackPreviewState

    data class Playing(
        val progress: Int,
    ) : TrackPreviewState
}