package com.pole.domain.model

data class UserData(
    val dateAddedToLibrary: Long? = null,
    val dateAddedToSchedule: Long? = null,
) {
    val library get() = dateAddedToLibrary != null
    val scheduled get() = dateAddedToSchedule != null
}
