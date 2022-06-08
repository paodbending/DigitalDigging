package com.pole.digitaldigging.screens.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pole.digitaldigging.R
import com.pole.domain.entities.Album

@Composable
fun AlbumLayout(album: Album, onClick: (Album) -> Unit) {
    Column(
        modifier = Modifier
            .padding(4.dp)
            .clickable { onClick(album) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            url = album.imageUrl,
            contentDescription = stringResource(id = R.string.album_cover),
            modifier = Modifier.size(150.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = album.name,
            fontSize = 16.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(IntrinsicSize.Min)
        )
        Text(
            text = album.artistNames.joinToString(separator = ", "),
            fontSize = 12.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            color = colorResource(id = R.color.spotify_gray_light),
            modifier = Modifier.width(IntrinsicSize.Min)
        )
        Text(
            text = album.releaseDate.year.toString(),
            fontSize = 12.sp,
            color = colorResource(id = R.color.spotify_gray_light),
            modifier = Modifier.width(IntrinsicSize.Min)
        )
    }
}