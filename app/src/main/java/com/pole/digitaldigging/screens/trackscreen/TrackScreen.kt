package com.pole.digitaldigging.screens.trackscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
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
fun TrackScreen(
    trackId: String,
    viewModel: TrackScreenViewModel = hiltViewModel(),
    onArtistClick: (artistId: String) -> Unit = { },
    onAlbumClick: (albumId: String) -> Unit = {},
    onTrackClick: (artistId: String) -> Unit = { },
) {
    LaunchedEffect(viewModel) {
        viewModel.updateState(trackId)
    }

    when (val state = viewModel.state.value) {
        TrackScreenState.Error -> {
            ErrorMessage()
        }
        TrackScreenState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is TrackScreenState.Ready -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    AsyncImage(
                        url = state.track.imageUrl,
                        contentDescription = stringResource(R.string.album_cover),
                        modifier = Modifier
                            .size(200.dp)
                            .padding(top = 8.dp)
                            .clickable { onAlbumClick(state.album.id) }
                    )
                    Text(
                        text = state.album.name,
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .clickable { onAlbumClick(state.album.id) },
                        color = colorResource(R.color.spotify_gray_light),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = state.track.name,
                        modifier = Modifier.padding(horizontal = 32.dp),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_baseline_explicit_24),
                            contentDescription = stringResource(R.string.explicit),
                            modifier = Modifier
                                .size(width = 13.dp, height = 16.dp)
                                .padding(end = 2.dp),
                            contentScale = ContentScale.Inside
                        )
                        Text(
                            text = state.track.duration,
                            color = colorResource(R.color.spotify_gray_light)
                        )
                    }
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
                        text = stringResource(R.string.recommended_tracks),
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                when (state.suggestedTracks) {
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
                        items(state.suggestedTracks.value.size) { i ->
                            val track = state.suggestedTracks.value[i]
                            TrackLayout(track) {
                                onTrackClick(it.id)
                            }
                        }
                    }
                }
            }
        }
    }
}