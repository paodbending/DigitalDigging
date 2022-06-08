package com.pole.digitaldigging.screens.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pole.digitaldigging.R
import com.pole.domain.entities.Track


@Composable
fun TrackLayout(track: Track, showTrackNumber: Boolean = false, onClick: (Track) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick(track) },
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (showTrackNumber) {
            Text(
                text = track.trackNumber.toString(),
                color = colorResource(id = R.color.spotify_gray_light),
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(min = 24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        AsyncImage(
            url = track.imageUrl,
            contentDescription = stringResource(id = R.string.album_cover),
            modifier = Modifier.size(50.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = track.name,
                overflow = TextOverflow.Ellipsis,
                fontSize = 15.sp,
                maxLines = 1
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                if (track.explicit) Image(
                    painter = painterResource(id = R.drawable.ic_baseline_explicit_24),
                    contentDescription = stringResource(id = R.string.explicit),
                    modifier = Modifier
                        .size(14.dp)
                        .padding(top = 1.dp, end = 2.dp),
                    colorFilter = ColorFilter.tint(colorResource(id = R.color.spotify_gray_light))
                )
                Text(
                    text = track.artistNames.joinToString(separator = ", "),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    fontSize = 12.sp,
                    color = colorResource(id = R.color.spotify_gray_light)
                )
            }
        }

        Text(
            text = track.duration
        )

        Spacer(modifier = Modifier.width(8.dp))
    }
}
