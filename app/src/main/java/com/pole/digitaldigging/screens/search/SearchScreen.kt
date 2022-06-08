package com.pole.digitaldigging.screens.search

import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.hilt.navigation.compose.hiltViewModel
import com.pole.digitaldigging.R
import com.pole.digitaldigging.UIResource
import com.pole.digitaldigging.screens.common.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchScreen(
    onArtistClick: (artistId: String) -> Unit = { },
    onAlbumClick: (artistId: String) -> Unit = { },
    onTrackClick: (artistId: String) -> Unit = { },
    viewModel: SearchViewModel = hiltViewModel(),
) = Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.fillMaxSize()
) {

    LaunchedEffect(viewModel) {
        viewModel.collectState()
    }

    val state = viewModel.state.value

    BackHandler(state.searchQuery.isNotEmpty()) {
        viewModel.search("")
    }

    val searchQuery = remember(state) { mutableStateOf(state.searchQuery) }
    TextField(
        value = searchQuery.value,
        onValueChange = {
            searchQuery.value = it
            viewModel.search(it)
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
            selected = state.searchSettings.searchType == SearchType.ALL
        ) {
            viewModel.setSearchType(SearchType.ALL)
        }

        SearchTypeSelector(
            text = stringResource(id = R.string.artists),
            selected = state.searchSettings.searchType == SearchType.ARTISTS
        ) {
            viewModel.setSearchType(SearchType.ARTISTS)
        }

        SearchTypeSelector(
            text = stringResource(id = R.string.albums),
            selected = state.searchSettings.searchType == SearchType.ALBUMS
        ) {
            viewModel.setSearchType(SearchType.ALBUMS)
        }

        SearchTypeSelector(
            text = stringResource(id = R.string.tracks),
            selected = state.searchSettings.searchType == SearchType.TRACKS
        ) {
            viewModel.setSearchType(SearchType.TRACKS)
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        when (state.searchSettings.searchType) {
            SearchType.ALL -> {}
            SearchType.ARTISTS -> {
                SearchTypeSelector(
                    text = stringResource(id = R.string.relevance),
                    selected = state.searchSettings.artistSortType == ArtistSortType.RELEVANCE
                ) {
                    viewModel.setArtistSortType(ArtistSortType.RELEVANCE)
                }

                SearchTypeSelector(
                    text = stringResource(id = R.string.popularity),
                    selected = state.searchSettings.artistSortType == ArtistSortType.POPULARITY
                ) {
                    viewModel.setArtistSortType(ArtistSortType.POPULARITY)
                }

                SearchTypeSelector(
                    text = stringResource(id = R.string.followers),
                    selected = state.searchSettings.artistSortType == ArtistSortType.FOLLOWERS
                ) {
                    viewModel.setArtistSortType(ArtistSortType.FOLLOWERS)
                }
            }
            SearchType.ALBUMS -> {
                SearchTypeSelector(
                    text = stringResource(id = R.string.relevance),
                    selected = state.searchSettings.albumSortType == AlbumSortType.RELEVANCE
                ) {
                    viewModel.setAlbumSortType(AlbumSortType.RELEVANCE)
                }

                SearchTypeSelector(
                    text = stringResource(id = R.string.popularity),
                    selected = state.searchSettings.albumSortType == AlbumSortType.POPULARITY
                ) {
                    viewModel.setAlbumSortType(AlbumSortType.POPULARITY)
                }

                SearchTypeSelector(
                    text = stringResource(id = R.string.release_date),
                    selected = state.searchSettings.albumSortType == AlbumSortType.RELEASE_DATE
                ) {
                    viewModel.setAlbumSortType(AlbumSortType.RELEASE_DATE)
                }
            }
            SearchType.TRACKS -> {
                SearchTypeSelector(
                    text = stringResource(id = R.string.relevance),
                    selected = state.searchSettings.trackSortType == TrackSortType.RELEVANCE
                ) {
                    viewModel.setTrackSortType(TrackSortType.RELEVANCE)
                }

                SearchTypeSelector(
                    text = stringResource(id = R.string.popularity),
                    selected = state.searchSettings.trackSortType == TrackSortType.POPULARITY
                ) {
                    viewModel.setTrackSortType(TrackSortType.POPULARITY)
                }

                SearchTypeSelector(
                    text = stringResource(id = R.string.length),
                    selected = state.searchSettings.trackSortType == TrackSortType.LENGTH
                ) {
                    viewModel.setTrackSortType(TrackSortType.LENGTH)
                }
            }
        }
    }

    when {
        state.searchQuery.isEmpty() -> {
            Message(
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
            ErrorMessage()
        }
        state.results is UIResource.Ready -> when (state.searchSettings.searchType) {
            SearchType.ALL -> {
                val results = state.results.value
                LazyColumn {

                    results.artists.firstOrNull()?.let {
                        item {
                            ArtistLayout(artist = it) { artist ->
                                onArtistClick(artist.id)
                            }
                        }
                    }

                    item {
                        LazyRow {
                            items(results.albums.size) { index ->
                                AlbumLayout(album = results.albums[index]) { album ->
                                    onAlbumClick(album.id)
                                }
                            }
                        }
                    }

                    items(results.tracks.size) { index ->
                        TrackLayout(track = results.tracks[index]) { track ->
                            onTrackClick(track.id)
                        }
                    }
                }
            }
            SearchType.ARTISTS -> {
                val artists = state.results.value.artists
                LazyColumn {
                    items(artists.size) { index ->
                        ArtistLayout(artist = artists[index]) { artist ->
                            onArtistClick(artist.id)
                        }
                    }
                }
            }
            SearchType.ALBUMS -> {
                val albums = state.results.value.albums
                LazyVerticalGrid(
                    cells = GridCells.Fixed(2)
                ) {
                    items(albums.size) { index ->
                        AlbumLayout(album = albums[index]) { album ->
                            onAlbumClick(album.id)
                        }
                    }
                }
            }
            SearchType.TRACKS -> {
                val tracks = state.results.value.tracks
                LazyColumn {
                    items(tracks.size) { index ->
                        TrackLayout(track = tracks[index]) { track ->
                            onTrackClick(track.id)
                        }
                    }
                }
            }
        }
    }
}