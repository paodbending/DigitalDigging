package com.pole.digitaldigging.screens.artistscreen

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pole.digitaldigging.R
import com.pole.digitaldigging.UIResource
import com.pole.digitaldigging.screens.common.AlbumLayout
import com.pole.digitaldigging.screens.common.AsyncImage
import com.pole.digitaldigging.screens.common.ErrorMessage
import kotlin.math.roundToInt

@Composable
fun ArtistScreen(
    artistId: String,
    onAlbumClick: (albumId: String) -> Unit = {},
    viewModel: ArtistScreenViewModel = hiltViewModel(),
) {

    LaunchedEffect(viewModel, artistId) {
        viewModel.collectState(artistId)
    }

    when (val state = viewModel.state.value) {
        ArtistScreenState.Error -> {
            ErrorMessage()
        }
        ArtistScreenState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is ArtistScreenState.Ready -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AsyncImage(
                            url = state.artist.imageUrl,
                            contentDescription = stringResource(id = R.string.artist_image),
                            modifier = Modifier
                                .size(128.dp)
                                .clip(shape = CircleShape)
                        )
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = state.artist.name,
                                fontSize = 20.sp
                            )
                            Text(
                                text = LocalContext.current.getFollowerString(state.artist.followers)
                            )
                        }
                    }
                }

                item {
                    when (state.albums) {
                        is UIResource.Error -> {
                            Spacer(modifier = Modifier.size(64.dp))
                            ErrorMessage()
                        }
                        is UIResource.Loading -> {
                            CircularProgressIndicator(modifier = Modifier.padding(top = 64.dp))
                        }
                        is UIResource.Ready -> {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = stringResource(id = R.string.albums),
                                    modifier = Modifier.padding(top = 8.dp),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                LazyRow(modifier = Modifier.padding(start = 8.dp, top = 8.dp)) {
                                    items(state.albums.value.albums.size) { i ->
                                        val album = state.albums.value.albums[i]
                                        AlbumLayout(
                                            album = album,
                                            onClick = { onAlbumClick(album.id) }
                                        )
                                    }
                                }

                                Text(
                                    text = stringResource(id = R.string.singles),
                                    modifier = Modifier.padding(top = 8.dp),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                LazyRow(modifier = Modifier.padding(start = 8.dp, top = 8.dp)) {
                                    items(state.albums.value.singles.size) { i ->
                                        val album = state.albums.value.singles[i]
                                        AlbumLayout(
                                            album = album,
                                            onClick = { onAlbumClick(album.id) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun Context.getFollowerString(followers: Int): String {
    return getString(
        R.string.s_followers, when {
            followers > 1_000_000_000 -> "${(followers / 1_000_000 * 1000.0).roundToInt() / 1000.0} B"
            followers > 1_000_000 -> "${(followers / 1_000_000 * 1000.0).roundToInt() / 1000.0} M"
            followers > 1_000 -> "${(followers / 1_000 * 1000.0).roundToInt() / 1000.0} K"
            else -> followers.toString()
        }
    )
}