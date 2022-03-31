package com.example.digitaldigging.screens.common.tracklist

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.digitaldigging.databinding.LayoutTrackBinding
import com.pole.domain.model.spotify.Track

class TrackViewHolder(
    private val showTrackNumber: Boolean,
    private val binding: LayoutTrackBinding,
    private val onClick: (Track) -> Unit
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
            }
        }
}