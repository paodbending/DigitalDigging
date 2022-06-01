package com.pole.digitaldigging.screens.search

import androidx.lifecycle.LiveData
import com.pole.digitaldigging.UIResource

interface Model {
    val searchQuery: String
    val searchSettings: SearchSettings
    val results: LiveData<UIResource<Results>>

    fun setSearchQuery(searchQuery: String)
    fun setSearchSettings(searchSettings: SearchSettings)
}