package com.pole.trackpreview

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MediaPlayerTrackPreviewPlayer @Inject constructor() : ViewModel() {

    private var currentUrl: String? = null

    private val mutableState: MutableLiveData<TrackPreviewState> =
        MutableLiveData(TrackPreviewState.NotAvailable)

    val state: LiveData<TrackPreviewState> = mutableState

    private val mediaPlayer: MediaPlayer = MediaPlayer().apply {
        setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )

        setOnErrorListener { _, _, _ ->
            mutableState.value = TrackPreviewState.NotAvailable
            false
        }

        setOnPreparedListener {
            mutableState.value = TrackPreviewState.Ready
        }

        setOnCompletionListener {
            mutableState.value = TrackPreviewState.Ready
        }
    }

    fun play() {
        mediaPlayer.seekTo(0)
        mediaPlayer.start()
        viewModelScope.launch(Dispatchers.Default) {
            while (mediaPlayer.isPlaying) {
                mutableState.postValue(TrackPreviewState.Playing(
                    100 * mediaPlayer.currentPosition / mediaPlayer.duration
                ))
                delay(1000 / 60)
            }
            mutableState.postValue(TrackPreviewState.Ready)
        }
    }

    fun pause() {
        mediaPlayer.pause()
        mutableState.value = TrackPreviewState.Ready
    }

    fun setUrl(url: String?) {
        if (currentUrl == url) return
        currentUrl = url
        mediaPlayer.reset()
        if (url != null) {
            mutableState.value = TrackPreviewState.Loading
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
        } else {
            mutableState.value = TrackPreviewState.NotAvailable
        }
    }

    override fun onCleared() {
        mediaPlayer.release()
    }
}