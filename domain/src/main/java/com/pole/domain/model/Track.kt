package com.pole.domain.model

private const val SECOND = 1000
private const val MINUTE = 60 * SECOND
private const val HOUR = 60 * MINUTE

private fun Int.toTrackDuration(): String {
    return when {
        this < HOUR -> "${(this / MINUTE) % 60}:${toDoubleDigit((this / SECOND) % 60)}"
        else -> "${this / HOUR}:${toDoubleDigit((this / MINUTE) % 60)}:${toDoubleDigit((this / SECOND) % 60)}"
    }
}

private fun toDoubleDigit(value: Int): String {
    return String.format("%02d", value)
}

data class Track(
    val spotifyId: String,
    val spotifyUrl: String?,

    val name: String,
    val artists: List<Artist>,

    val previewUrl: String? = null,

    val type: String,
    val trackNumber: Int,
    val discNumber: Int,
    val explicit: Boolean,

    val length: Int,
    val duration: String = length.toTrackDuration(),

    val popularity: Int? = null
)

data class TrackInfo(
    val track: Track,

    val album: Album,
)
