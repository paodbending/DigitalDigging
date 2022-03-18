package com.pole.domain.model


data class Image(
    val url: String,
)

data class ReleaseDate(val year: Int, val month: Int?, val day: Int?)
