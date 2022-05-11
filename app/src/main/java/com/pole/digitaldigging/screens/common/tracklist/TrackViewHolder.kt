package com.pole.digitaldigging.screens.common.tracklist

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pole.digitaldigging.databinding.LayoutTrackBinding
import com.pole.domain.entities.Track

class TrackViewHolder(
    private val showTrackNumber: Boolean,
    private val binding: LayoutTrackBinding,
    private val onClick: (Track) -> Unit,
) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener { track?.let(onClick) }
        binding.trackNumberTextView.visibility =
            if (showTrackNumber) View.VISIBLE else View.GONE
    }

    var track: Track? = null
        set(value) {
            field = value?.also {

                if (showTrackNumber) binding.trackNumberTextView.text = it.trackNumber.toString()

                binding.trackNameTextView.text = it.name

                binding.durationTextView.text = it.duration

                binding.explicitImageView.visibility = if (it.explicit) View.VISIBLE else View.GONE

                binding.trackArtistsTextView.text = it.artistNames.joinToString(separator = ", ")

                Glide
                    .with(binding.root)
                    .load(it.imageUrl)
                    .centerInside()
                    .into(binding.trackImageView)
            }
        }
}