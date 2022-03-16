package com.pole.domain.model

data class Image(
    val height: Int? = null,
    val url: String,
    val width: Int? = null
)

data class ReleaseDate(val year: Int, val month: Int?, val day: Int?)
