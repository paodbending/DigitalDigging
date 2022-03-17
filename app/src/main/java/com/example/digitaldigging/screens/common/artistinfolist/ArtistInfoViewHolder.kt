package com.example.digitaldigging.screens.common.artistinfolist

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.digitaldigging.databinding.LayoutArtistBinding
import com.pole.domain.model.ArtistInfo

class ArtistInfoViewHolder(
    private val binding: LayoutArtistBinding,
    onClick: (ArtistInfo) -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {

    private var artistInfo: ArtistInfo? = null

    init {
        binding.root.setOnClickListener {
            artistInfo?.let(onClick)
        }
    }

    fun bind(artistInfo: ArtistInfo) {
        this.artistInfo = artistInfo

        val firstImage = artistInfo.images.firstOrNull()
        if (firstImage != null) {
            Glide
                .with(binding.root)
                .load(firstImage.url)
                .centerCrop()
                .into(binding.artistImageView)
        }

        binding.artistNameTextView.text = artistInfo.artist.name
    }
}