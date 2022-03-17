package com.example.digitaldigging.screens.common.albumlist

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.digitaldigging.databinding.LayoutAlbumBinding
import com.pole.domain.model.Album

class AlbumViewHolder(private val binding: LayoutAlbumBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(album: Album) {
        val firstImage = album.images.firstOrNull()
        if (firstImage != null) {
            Glide
                .with(binding.root)
                .load(firstImage.url)
                .centerCrop()
                .into(binding.albumImageView)
        }
        binding.albumNameTextView.text = album.name
    }
}