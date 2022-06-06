package com.pole.digitaldigging

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import java.util.concurrent.TimeoutException

/* Copyright 2019 Google LLC.
   SPDX-License-Identifier: Apache-2.0 */
suspend fun <T> LiveData<T>.getOrAwaitValue(
    milliseconds: Long = 1000,
): T? {
    val channel = Channel<T?>(Channel.CONFLATED)

    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            this@getOrAwaitValue.removeObserver(this)
            runBlocking {
                channel.send(o)
            }
        }
    }

    observeForever(observer)

    // Don't wait indefinitely if the LiveData is not set.
    return try {
        withTimeout(milliseconds) {
            channel.receive()
        }
    } catch (e: Exception) {
        throw TimeoutException("LiveData value was never set.")
    }
}

suspend inline fun <T> LiveData<T>.observeForTesting(block: (LiveDataTestContext<T>) -> Unit) {

    val channel = Channel<T?>(Channel.CONFLATED)

    val observer = Observer<T> { o ->
        runBlocking {
            channel.send(o)
        }
    }

    val context: LiveDataTestContext<T> = LiveDataTestContext(this, channel)

    observeForever(observer)

    block(context)

    removeObserver(observer)
}


class LiveDataTestContext<T : Any?>(
    liveData: LiveData<T>,
    private val channel: Channel<T?>,
) {
    val value: T? = liveData.value

    suspend fun awaitValue(milliseconds: Long? = null): T? {
        return if (milliseconds == null) channel.receive()
        else {
            try {
                withTimeout(milliseconds) {
                    channel.receive()
                }
            } catch (e: Exception) {
                throw TimeoutException("LiveData value was never set.")
            }
        }
    }
}