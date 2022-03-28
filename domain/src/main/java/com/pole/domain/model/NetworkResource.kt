package com.pole.domain.model

sealed interface NetworkResource<T> {
    class Loading<T> : NetworkResource<T>

    class Error<T> : NetworkResource<T>

    data class Ready<T>(
        val value: T
    ) : NetworkResource<T>
}

