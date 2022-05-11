package com.pole.digitaldigging

import com.pole.domain.entities.NetworkResource
import com.pole.domain.AppError

sealed interface UIResource<T> {

    class Loading<T> : UIResource<T>

    data class Error<T>(
        val appError: AppError,
    ) : UIResource<T>

    data class Ready<T>(
        val value: T,
    ) : UIResource<T>
}

fun <T> NetworkResource<T>.toUIResource(): UIResource<T> {
    return when (this) {
        is NetworkResource.Error -> UIResource.Error(appError)
        is NetworkResource.Loading -> UIResource.Loading()
        is NetworkResource.Ready -> UIResource.Ready(value)
    }
}