package com.pole.domain.model

data class SpotifyEntityUserData(
    val id: String,
    val type: SpotifyEntityType,
    val dateAddedToLibrary: Long?,
    val dateAddedToSchedule: Long?,
) {
    val library get() = dateAddedToLibrary != null
    val scheduled get() = dateAddedToSchedule != null
}
