package com.pole.data.databases.spotifycache.search

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE

@Entity(
    tableName = "search_result",
    primaryKeys = ["searchQuery", "id"],
    foreignKeys = [
        ForeignKey(
            entity = SearchRequest::class,
            parentColumns = ["searchQuery"],
            childColumns = ["searchQuery"],
            onDelete = CASCADE
        ),
    ],
)
internal data class CachedSearchResult(
    val searchQuery: String,
    val id: String,
    val resultOrder: Int,
    val type: String
)