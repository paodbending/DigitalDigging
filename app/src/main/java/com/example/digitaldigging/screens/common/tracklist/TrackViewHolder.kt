package com.example.digitaldigging.screens.common.tracklist

import androidx.recyclerview.widget.RecyclerView
import com.example.digitaldigging.databinding.LayoutTrackBinding
import com.pole.domain.model.Track

class TrackViewHolder(private val binding: LayoutTrackBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(track: Track) {
        binding.trackNameTextView.text = track.name
    }
}