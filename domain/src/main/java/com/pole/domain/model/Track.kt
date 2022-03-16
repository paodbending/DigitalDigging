package com.pole.domain.model


data class Track(
    val spotifyId: String,
    val spotifyUrl: String?,

    val name: String,
    val artists: List<Artist>,

    val previewUrl: String? = null,

    val type: String,
    val trackNumber: Int,
    val discNumber: Int,
    val length: Int,
    val explicit: Boolean,

    val popularity: Int? = null,
)

data class TrackInfo(
    val track: Track,

    val album: Album,
)
