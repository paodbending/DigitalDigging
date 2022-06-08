package com.pole.digitaldigging.screens.common

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

@Composable
fun AsyncImage(
    url: String?,
    contentDescription: String,
    modifier: Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    @DrawableRes placeHolder: Int? = null,
) {

    val imageBitmap = remember {
        mutableStateOf<ImageBitmap?>(null)
    }

    val customLoader = remember {
        object : CustomTarget<Bitmap?>() {
            override fun onResourceReady(
                image: Bitmap,
                transition: Transition<in Bitmap?>?,
            ) {
                imageBitmap.value = image.asImageBitmap()
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                imageBitmap.value = null
            }
        }
    }

    val context = LocalContext.current

    LaunchedEffect(url) {

        // Cancel any previous ongoing loading for this image
        Glide.with(context).clear(customLoader)

        // Reset current displayed bitmap with placeholder (if any) or null
        imageBitmap.value =
            placeHolder?.let { ContextCompat.getDrawable(context, it)?.toBitmap()?.asImageBitmap() }

        // Try to load actual image
        Glide.with(context).asBitmap().load(url).into(customLoader)
    }

    DisposableEffect(true) {
        onDispose {
            // Cancel any ongoing loading for this image
            // when leaving the composition
            Glide.with(context).clear(customLoader)
        }
    }

    AsyncImageContent(
        imageBitmap = imageBitmap.value,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale
    )
}

@Composable
private fun AsyncImageContent(
    imageBitmap: ImageBitmap?,
    contentDescription: String,
    modifier: Modifier,
    contentScale: ContentScale,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap,
                contentDescription = contentDescription,
                contentScale = contentScale
            )
        } else CircularProgressIndicator()
    }
}