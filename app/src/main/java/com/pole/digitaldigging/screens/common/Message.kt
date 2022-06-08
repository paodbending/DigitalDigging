package com.pole.digitaldigging.screens.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.pole.digitaldigging.R

@Composable
fun Message(text: String, resId: Int) = Column {
    Spacer(
        modifier = Modifier
            .weight(0.2f)
            .background(Color.Red)
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painterResource(resId),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(64.dp),
            colorFilter = ColorFilter.tint(colorResource(id = R.color.spotify_gray_light))
        )
        Spacer(
            modifier = Modifier.height(32.dp)
        )
        Text(
            text = text,
            fontStyle = FontStyle.Italic,
            color = colorResource(id = R.color.spotify_gray_light)
        )
    }
    Spacer(
        modifier = Modifier
            .weight(0.8f)
            .background(Color.Red)
    )
}