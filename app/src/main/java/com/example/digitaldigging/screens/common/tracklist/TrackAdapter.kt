package com.example.digitaldigging.screens.common.tracklist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.digitaldigging.databinding.LayoutTrackBinding
import com.pole.domain.model.spotify.Track

class TrackAdapter(
    private val showTrackNumber: Boolean = false,
    private val onClick: (Track) -> Unit
) :
    ListAdapter<Track, TrackViewHolder>(DiffCallback) {

    private object DiffCallback : DiffUtil.ItemCallback<Track>() {
        override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = LayoutTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(showTrackNumber, binding, onClick)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.track = currentList[position]
    }
}