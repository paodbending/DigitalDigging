package com.pole.digitaldigging.screens.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pole.digitaldigging.UIResource
import com.pole.domain.entities.NetworkResource
import com.pole.domain.usecases.GetSearchResults
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

class ModelImpl(
    private val getSearchResults: GetSearchResults,
    private val coroutineScope: CoroutineScope,
    private val defaultDispatcher: CoroutineDispatcher,
) : Model {

    override var searchQuery = ""
        private set

    override var searchSettings: SearchSettings = SearchSettings()
        private set

    private val mutableResults: MutableLiveData<UIResource<Results>> = MutableLiveData()
    override val results: LiveData<UIResource<Results>> = mutableResults

    private var job: Job? = null

    override fun setSearchQuery(searchQuery: String) {
        this.searchQuery = searchQuery

        job?.cancel()
        job = coroutineScope.launch(defaultDispatcher) { updateResults() }
    }

    override fun setSearchSettings(searchSettings: SearchSettings) {
        this.searchSettings = searchSettings

        job?.cancel()
        job = coroutineScope.launch(defaultDispatcher) { updateResults() }
    }

    private suspend fun updateResults() {
        if (searchQuery.isEmpty()) {
            mutableResults.postValue(UIResource.Ready(Results(
                artists = emptyList(),
                albums = emptyList(),
                tracks = emptyList()
            )))
        } else {
            delay(250)
            getSearchResults(searchQuery).collectLatest { searchResults ->
                mutableResults.postValue(when (searchResults) {
                    is NetworkResource.Ready -> UIResource.Ready(
                        Results(
                            artists = searchResults.value.artists,
                            albums = searchResults.value.albums,
                            tracks = searchResults.value.tracks
                        )
                    )
                    is NetworkResource.Error -> UIResource.Error(searchResults.appError)
                    is NetworkResource.Loading -> UIResource.Loading()
                })
            }
        }
    }
}