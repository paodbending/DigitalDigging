package com.pole.domain.model.spotify

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
    val id: String,
    val spotifyUrl: String?,

    val name: String,
    val artistIds: List<String>,

    val previewUrl: String? = null,

    val type: String,
    val trackNumber: Int,
    val discNumber: Int,
    val explicit: Boolean,

    val length: Int,

    val popularity: Int,

    val imageUrl: String?,
    val albumId: String,
    val artistNames: List<String>
) {
    val duration: String = length.toTrackDuration()
}