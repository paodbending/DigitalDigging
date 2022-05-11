package com.pole.digitaldigging.screens.common.albumlist

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pole.digitaldigging.databinding.LayoutAlbumBinding
import com.pole.domain.entities.Album

class AlbumViewHolder(
    private val binding: LayoutAlbumBinding,
    private val onClick: (Album) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    var album: Album? = null
        set(value) {
            field = value?.also { album ->
                Glide
                    .with(binding.root)
                    .load(album.imageUrl)
                    .centerCrop()
                    .into(binding.albumImageView)

                binding.albumNameTextView.text = album.name
                binding.artistNameTextView.text = album.artistNames.joinToString(", ")
                binding.albumYearTextView.text = album.releaseDate.year.toString()
            }
        }

    init {
        binding.root.setOnClickListener { album?.let(onClick) }
    }
}