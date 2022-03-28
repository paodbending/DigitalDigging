package com.pole.data.databases.spotifycache.search

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pole.data.databases.spotifycache.CachedValue

@Entity(tableName = "search_request")
internal data class SearchRequest(
    @PrimaryKey val searchQuery: String
) : CachedValue(System.currentTimeMillis())