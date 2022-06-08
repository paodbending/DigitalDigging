package com.pole.digitaldigging.screens.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pole.digitaldigging.R
import com.pole.domain.entities.Artist


@Composable
fun ArtistLayout(artist: Artist, onClick: (Artist) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clickable { onClick(artist) }
    ) {

        AsyncImage(
            url = artist.imageUrl,
            contentDescription = stringResource(id = R.string.artist_image),
            modifier = Modifier
                .size(48.dp)
                .clip(shape = CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = artist.name,
            fontSize = 16.sp
        )
    }
}