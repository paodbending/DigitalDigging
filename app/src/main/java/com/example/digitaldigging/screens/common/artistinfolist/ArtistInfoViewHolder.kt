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

    init {
        binding.root.setOnClickListener { artistInfo?.let(onClick) }
    }

    var artistInfo: ArtistInfo? = null
        set(value) {
            field = value?.also {
                Glide
                    .with(binding.root)
                    .load(it.image?.url)
                    .centerInside()
                    .into(binding.artistImageView)

                binding.artistNameTextView.text = it.artist.name
            }
        }
}