package com.pole.digitaldigging.screens.albumscreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pole.digitaldigging.R
import com.pole.digitaldigging.UIResource
import com.pole.digitaldigging.screens.common.ArtistLayout
import com.pole.digitaldigging.screens.common.AsyncImage
import com.pole.digitaldigging.screens.common.ErrorMessage
import com.pole.digitaldigging.screens.common.TrackLayout


@Composable
fun AlbumScreen(
    albumId: String,
    onArtistClick: (artistId: String) -> Unit = { },
    onTrackClick: (artistId: String) -> Unit = { },
    viewModel: AlbumScreenViewModel = hiltViewModel(),
) {

    LaunchedEffect(viewModel, albumId) {
        viewModel.updateState(albumId)
    }

    when (val state = viewModel.state.value) {
        AlbumScreenState.Error -> {
            ErrorMessage()
        }
        AlbumScreenState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is AlbumScreenState.Ready -> {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    AsyncImage(
                        url = state.album.imageUrl,
                        contentDescription = stringResource(R.string.album_cover),
                        modifier = Modifier
                            .size(200.dp)
                            .padding(vertical = 16.dp)
                    )
                    Text(
                        text = state.album.name,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = state.album.releaseDate.year.toString(),
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = colorResource(R.color.spotify_gray_light)
                    )
                    Text(
                        text = stringResource(R.string.artists),
                        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                when (state.artists) {
                    is UIResource.Error -> {
                        item {
                            ErrorMessage()
                        }
                    }
                    is UIResource.Loading -> {
                        item {
                            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
                        }
                    }
                    is UIResource.Ready -> {
                        items(state.artists.value.size) { i ->
                            val artist = state.artists.value[i]
                            ArtistLayout(artist) {
                                onArtistClick(it.id)
                            }
                        }
                    }
                }
                item {
                    Text(
                        text = stringResource(R.string.tracks),
                        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                when (state.tracks) {
                    is UIResource.Error -> {
                        item {
                            ErrorMessage()
                        }
                    }
                    is UIResource.Loading -> {
                        item {
                            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
                        }
                    }
                    is UIResource.Ready -> {
                        items(state.tracks.value.size) { i ->
                            val track = state.tracks.value[i]
                            TrackLayout(track, showTrackNumber = true) {
                                onTrackClick(it.id)
                            }
                        }
                    }
                }
            }
        }
    }
}