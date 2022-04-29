package com.example.digitaldigging.screens.searchscreen.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.digitaldigging.screens.searchscreen.state.SearchScreenState
import com.example.digitaldigging.screens.searchscreen.SearchScreenViewModel
import com.example.digitaldigging.screens.searchscreen.state.data.SearchQueryData
import com.example.digitaldigging.vhs.rememberStateFrom


// Simple Composable screen used as an example.
// It is not complete nor actually used inside the application.
@Composable
fun SearchScreen(stateHolder: SearchScreenViewModel) {
    val state = rememberStateFrom(stateHolder)
    SearchScreen(state.value)
}

@Composable
private fun SearchScreen(state: SearchScreenState) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        TextField(
            value = if (state is SearchQueryData) state.searchQuery else "",
            onValueChange = { state.setSearchQuery(it) },
            modifier = Modifier.fillMaxWidth()
        )

        when (state) {
            is SearchScreenState.Idle -> {
                Text(text = "Search something!")
            }
            is SearchScreenState.Loading -> {
                CircularProgressIndicator()
            }
            is SearchScreenState.NetworkError -> {
                Text(text = "Network Error!")
            }
            is SearchScreenState.Results -> {
                LazyColumn {
                    items(state.artists) { artist ->
                        Text(
                            text = artist.name,
                            modifier = Modifier.clickable {
                                state.onArtistClick(artist.id)
                            }
                        )
                    }
                }
            }
        }
    }
}