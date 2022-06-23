package com.pole.trackpreview.impl

import android.media.AudioAttributes
import android.media.MediaPlayer
import com.pole.trackpreview.api.TrackPreviewPlayer
import com.pole.trackpreview.api.TrackPreviewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class TrackPreviewPlayerMediaPlayerImpl @Inject constructor() : TrackPreviewPlayer() {

    private val coroutineScope = MainScope()

    private var currentUrl: String? = null

    private val mediaPlayer: MediaPlayer = MediaPlayer().apply {
        setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )

        setOnErrorListener { _, _, _ ->
            value = TrackPreviewState.NotAvailable
            true
        }

        setOnPreparedListener {
            value = TrackPreviewState.Ready
        }

        setOnCompletionListener {
            value = TrackPreviewState.Ready
        }
    }

    override fun setUrl(url: String?) {
        if(url == null) {
            currentUrl = null
            value = TrackPreviewState.NotAvailable
            return
        } else if(currentUrl != url) {
            currentUrl = url
            value = TrackPreviewState.Loading
            mediaPlayer.reset()
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
        }
    }

    override fun play() {
        mediaPlayer.seekTo(0)
        mediaPlayer.start()
        coroutineScope.launch(Dispatchers.Default) {
            while (mediaPlayer.isPlaying) {
                postValue(TrackPreviewState.Playing(
                    100 * mediaPlayer.currentPosition / mediaPlayer.duration
                ))
                delay(1000 / 60)
            }
            postValue(TrackPreviewState.Ready)
        }
    }

    override fun pause() {
        mediaPlayer.pause()
        value = TrackPreviewState.Ready
    }
}