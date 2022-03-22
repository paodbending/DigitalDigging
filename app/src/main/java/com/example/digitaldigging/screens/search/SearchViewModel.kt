package com.example.digitaldigging.screens.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pole.domain.usecases.SearchArtist
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchArtist: SearchArtist
) : ViewModel() {

    private val _query = MutableLiveData("")
    val query: LiveData<String> = _query

    private val _state = MutableLiveData<SearchState>(Idle)
    val state: LiveData<SearchState> = _state

    private var searchJob: Job? = null

    fun search(query: String) {

        if (query == _query.value) return

        // Update Query value
        _query.value = query

        searchJob?.cancel()

        searchJob = viewModelScope.safeLaunch {

            _state.postValue(Loading)

            delay(250)

            // Retrieve last value
            val currentQuery = _query.value

            if (currentQuery.isNullOrEmpty()) {
                _state.value = Idle
            } else {
                val artists = searchArtist(currentQuery)

                _state.postValue(
                    SearchResults(
                        artists
                    )
                )
            }

            withContext(Dispatchers.Main) {
                searchJob = null
            }
        }
    }

    fun CoroutineScope.safeLaunch(block: suspend CoroutineScope.() -> Unit): Job {
        return this.launch {
            try {
                block()
            } catch (ce: CancellationException) {
                // You can ignore or log this exception
            } catch (e: Exception) {
                // Here it's better to at least log the exception
                Log.e("TAG", "Coroutine error", e)
            }
        }
    }
}