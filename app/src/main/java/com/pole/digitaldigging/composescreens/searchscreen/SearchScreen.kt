package com.pole.digitaldigging.composescreens.searchscreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import com.pole.digitaldigging.R
import com.pole.digitaldigging.UIResource
import com.pole.digitaldigging.composescreens.common.AlbumLayout
import com.pole.digitaldigging.composescreens.common.ArtistLayout
import com.pole.digitaldigging.composescreens.common.TrackLayout
import com.pole.digitaldigging.state.search.SearchScreenState
import com.pole.digitaldigging.state.search.intent.SetSearchTypeIntent

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchScreen(state: SearchScreenState) = Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.fillMaxSize()
) {
    val searchQuery = remember(state) { mutableStateOf(state.searchQuery) }
    TextField(
        value = searchQuery.value,
        onValueChange = {
            searchQuery.value = it
            state.setSearchQuery(it)
        },
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
        singleLine = true,
        placeholder = {
            Text(
                text = stringResource(id = R.string.search_hint),
                color = colorResource(id = R.color.spotify_gray_dark),
                fontStyle = FontStyle.Italic
            )
        }
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        SearchTypeSelector(
            text = stringResource(id = R.string.best_results),
            selected = state is SearchScreenState.BestResults
        ) {
            state.setSearchType(SetSearchTypeIntent.SearchType.BEST_RESULTS)
        }

        SearchTypeSelector(
            text = stringResource(id = R.string.artists),
            selected = state is SearchScreenState.Artists
        ) {
            state.setSearchType(SetSearchTypeIntent.SearchType.ARTISTS)
        }

        SearchTypeSelector(
            text = stringResource(id = R.string.albums),
            selected = state is SearchScreenState.Albums
        ) {
            state.setSearchType(SetSearchTypeIntent.SearchType.ALBUMS)
        }

        SearchTypeSelector(
            text = stringResource(id = R.string.tracks),
            selected = state is SearchScreenState.Tracks
        ) {
            state.setSearchType(SetSearchTypeIntent.SearchType.TRACKS)
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        when (state) {
            is SearchScreenState.BestResults -> {}
            is SearchScreenState.Artists -> {
                SearchTypeSelector(
                    text = stringResource(id = R.string.relevance),
                    selected = state.sortBy == SearchScreenState.Artists.SortBy.RELEVANCE
                ) {
                    state.setArtistSortType(SearchScreenState.Artists.SortBy.RELEVANCE)
                }

                SearchTypeSelector(
                    text = stringResource(id = R.string.popularity),
                    selected = state.sortBy == SearchScreenState.Artists.SortBy.POPULARITY
                ) {
                    state.setArtistSortType(SearchScreenState.Artists.SortBy.POPULARITY)
                }

                SearchTypeSelector(
                    text = stringResource(id = R.string.followers),
                    selected = state.sortBy == SearchScreenState.Artists.SortBy.FOLLOWERS
                ) {
                    state.setArtistSortType(SearchScreenState.Artists.SortBy.FOLLOWERS)
                }
            }
            is SearchScreenState.Albums -> {
                SearchTypeSelector(
                    text = stringResource(id = R.string.relevance),
                    selected = state.sortBy == SearchScreenState.Albums.SortBy.RELEVANCE
                ) {
                    state.setAlbumSortType(SearchScreenState.Albums.SortBy.RELEVANCE)
                }

                SearchTypeSelector(
                    text = stringResource(id = R.string.popularity),
                    selected = state.sortBy == SearchScreenState.Albums.SortBy.POPULARITY
                ) {
                    state.setAlbumSortType(SearchScreenState.Albums.SortBy.POPULARITY)
                }

                SearchTypeSelector(
                    text = stringResource(id = R.string.release_date),
                    selected = state.sortBy == SearchScreenState.Albums.SortBy.RELEASE_DATE
                ) {
                    state.setAlbumSortType(SearchScreenState.Albums.SortBy.RELEASE_DATE)
                }
            }
            is SearchScreenState.Tracks -> {
                SearchTypeSelector(
                    text = stringResource(id = R.string.relevance),
                    selected = state.sortBy == SearchScreenState.Tracks.SortBy.RELEVANCE
                ) {
                    state.setTrackSortType(SearchScreenState.Tracks.SortBy.RELEVANCE)
                }

                SearchTypeSelector(
                    text = stringResource(id = R.string.popularity),
                    selected = state.sortBy == SearchScreenState.Tracks.SortBy.POPULARITY
                ) {
                    state.setTrackSortType(SearchScreenState.Tracks.SortBy.POPULARITY)
                }

                SearchTypeSelector(
                    text = stringResource(id = R.string.length),
                    selected = state.sortBy == SearchScreenState.Tracks.SortBy.LENGTH
                ) {
                    state.setTrackSortType(SearchScreenState.Tracks.SortBy.LENGTH)
                }
            }
        }
    }

    when (state) {
        is SearchScreenState.BestResults -> {
            when {
                state.searchQuery.isEmpty() -> {
                    Content(
                        text = stringResource(id = R.string.search_something),
                        resId = R.drawable.ic_baseline_music_note_24
                    )
                }
                state.results is UIResource.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                state.results is UIResource.Error -> {
                    Content(
                        text = stringResource(id = R.string.no_internet_connection_error),
                        resId = R.drawable.ic_baseline_signal_wifi_connected_no_internet_4_24
                    )
                }

                state.results is UIResource.Ready -> {
                    val results = state.results.value
                    LazyColumn {

                        if (results.artist != null) item {
                            ArtistLayout(artist = results.artist) { artist ->
                                state.onArtistClick(artist.id)
                            }
                        }

                        item {
                            LazyRow {
                                items(results.albums.size) { index ->
                                    AlbumLayout(album = results.albums[index])
                                }
                            }
                        }

                        items(results.tracks.size) { index ->
                            TrackLayout(track = results.tracks[index])
                        }
                    }
                }
            }
        }
        is SearchScreenState.Artists -> {
            when {
                state.searchQuery.isEmpty() -> {
                    Content(
                        text = stringResource(id = R.string.search_something),
                        resId = R.drawable.ic_baseline_music_note_24
                    )
                }
                state.artists is UIResource.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                state.artists is UIResource.Error -> {
                    Content(
                        text = stringResource(id = R.string.no_internet_connection_error),
                        resId = R.drawable.ic_baseline_signal_wifi_connected_no_internet_4_24
                    )
                }
                state.artists is UIResource.Ready -> {
                    val artists = state.artists.value
                    LazyColumn {
                        items(artists.size) { index ->
                            ArtistLayout(artist = artists[index]) { artist ->
                                state.onArtistClick(artist.id)
                            }
                        }
                    }
                }
            }
        }
        is SearchScreenState.Albums -> {
            when {
                state.searchQuery.isEmpty() -> {
                    Content(
                        text = stringResource(id = R.string.search_something),
                        resId = R.drawable.ic_baseline_music_note_24
                    )
                }
                state.albums is UIResource.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                state.albums is UIResource.Error -> {
                    Content(
                        text = stringResource(id = R.string.no_internet_connection_error),
                        resId = R.drawable.ic_baseline_signal_wifi_connected_no_internet_4_24
                    )
                }
                state.albums is UIResource.Ready -> {
                    val albums = state.albums.value
                    LazyVerticalGrid(
                        cells = GridCells.Fixed(2)
                    ) {
                        items(albums.size) { index ->
                            AlbumLayout(album = albums[index])
                        }
                    }
                }
            }
        }
        is SearchScreenState.Tracks -> {
            when {
                state.searchQuery.isEmpty() -> {
                    Content(
                        text = stringResource(id = R.string.search_something),
                        resId = R.drawable.ic_baseline_music_note_24
                    )
                }
                state.tracks is UIResource.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                state.tracks is UIResource.Error -> {
                    Content(
                        text = stringResource(id = R.string.no_internet_connection_error),
                        resId = R.drawable.ic_baseline_signal_wifi_connected_no_internet_4_24
                    )
                }
                state.tracks is UIResource.Ready -> {
                    val tracks = state.tracks.value
                    LazyColumn {
                        items(tracks.size) { index ->
                            TrackLayout(track = tracks[index])
                        }
                    }
                }
            }
        }
    }
}
