package com.pole.digitaldigging.composescreens.searchscreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pole.digitaldigging.R


@Composable
fun SearchTypeSelector(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(4.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) colorResource(id = R.color.spotify_green_light) else colorResource(
                id = R.color.spotify_gray_dark)
        ),
        colors = ButtonDefaults.buttonColors(backgroundColor = if (selected) colorResource(id = R.color.spotify_green) else Color.Transparent),
        modifier = Modifier.defaultMinSize(
            minWidth = ButtonDefaults.MinWidth,
            minHeight = 33.dp
        )
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
        )
    }
}