package com.pole.digitaldigging.screens.common.artistlist

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pole.digitaldigging.databinding.LayoutArtistBinding
import com.pole.domain.model.spotify.Artist

class ArtistViewHolder(
    private val binding: LayoutArtistBinding,
    onClick: (Artist) -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener { artist?.let(onClick) }
    }

    var artist: Artist? = null
        set(value) {
            field = value?.also {
                Glide
                    .with(binding.root)
                    .load(it.imageUrl)
                    .centerInside()
                    .into(binding.artistImageView)

                binding.artistNameTextView.text = it.name
            }
        }
}