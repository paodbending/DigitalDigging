package com.pole.digitaldigging.composescreens

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.Glide
import com.pole.digitaldigging.R
import com.pole.digitaldigging.UIResource
import com.pole.digitaldigging.databinding.FragmentArtistScreenBinding
import com.pole.digitaldigging.screens.common.albumlist.AlbumAdapter
import com.pole.digitaldigging.state.artist.ArtistScreenState
import kotlin.math.roundToInt

@Composable
fun ArtistScreen(state: ArtistScreenState) {

    val context = LocalContext.current

    val binding = remember {
        FragmentArtistScreenBinding.inflate(LayoutInflater.from(context), null, false)
    }

    val albumsAdapter = remember {
        AlbumAdapter {}
    }

    val singlesAdapter = remember {
        AlbumAdapter {}
    }

    BackHandler(true) {
        state.onBackPress()
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = {

            binding.albumsRecyclerView.adapter = albumsAdapter
            binding.singlesRecyclerView.adapter = singlesAdapter

            binding.root
        },
        update = {

            binding.networkErrorLayout.root.visibility =
                if (state is ArtistScreenState.Error) View.VISIBLE else View.GONE
            binding.progressCircular.visibility =
                if (state is ArtistScreenState.Loading) View.VISIBLE else View.GONE
            binding.readyLayout.visibility =
                if (state is ArtistScreenState.Ready) View.VISIBLE else View.GONE

            if (state is ArtistScreenState.Ready) {

                binding.artistNameTextView.text = state.artist.name
                binding.followersCountTextView.text =
                    context.getFollowerString(state.artist.followers)

                Glide
                    .with(binding.root)
                    .load(state.artist.imageUrl)
                    .centerCrop()
                    .into(binding.artistImageView)

                binding.albumsProgressCircular.visibility =
                    if (state.albums is UIResource.Loading) View.VISIBLE else View.GONE

                binding.albumsErrorLayout.root.visibility =
                    if (state.albums is UIResource.Error) View.VISIBLE else View.GONE

                binding.albumLayout.visibility =
                    if (state.albums is UIResource.Ready) View.VISIBLE else View.GONE

                if (state.albums is UIResource.Ready) {
                    albumsAdapter.submitList(state.albums.value.albums)
                    singlesAdapter.submitList(state.albums.value.singles)
                }
            }
        }
    )
}

private fun Context.getFollowerString(followers: Int?): String? {
    return if (followers == null) null else getString(
        R.string.s_followers, when {
            followers > 1_000_000_000 -> "${(followers / 1_000_000 * 1000.0).roundToInt() / 1000.0} B"
            followers > 1_000_000 -> "${(followers / 1_000_000 * 1000.0).roundToInt() / 1000.0} M"
            followers > 1_000 -> "${(followers / 1_000 * 1000.0).roundToInt() / 1000.0} K"
            else -> followers.toString()
        }
    )
}