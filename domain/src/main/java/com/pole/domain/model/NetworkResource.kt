package com.pole.domain.model

import com.pole.domain.model.error.AppError

sealed interface NetworkResource<T> {

    class Loading<T> : NetworkResource<T>

    data class Error<T>(
        val appError: AppError,
    ) : NetworkResource<T>

    data class Ready<T>(
        val value: T,
    ) : NetworkResource<T>
}