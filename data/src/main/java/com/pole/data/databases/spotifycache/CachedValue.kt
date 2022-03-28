package com.pole.data.databases.spotifycache

internal abstract class CachedValue(
    var lastUpdated: Long
) {
    val isNotValid: Boolean get() = (System.currentTimeMillis() - lastUpdated) > 604800000 // one week
}