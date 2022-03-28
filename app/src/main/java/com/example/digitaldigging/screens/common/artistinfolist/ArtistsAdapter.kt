package com.example.digitaldigging.screens.common.artistinfolist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.digitaldigging.databinding.LayoutArtistBinding
import com.pole.domain.model.spotify.Artist

class ArtistsAdapter(private val onClick: (Artist) -> Unit) :
    ListAdapter<Artist, ArtistViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val binding =
            LayoutArtistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArtistViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        holder.artist = currentList[position]
    }

    private object DiffCallback : DiffUtil.ItemCallback<Artist>() {
        override fun areItemsTheSame(oldItem: Artist, newItem: Artist): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Artist, newItem: Artist): Boolean {
            return oldItem == newItem
        }
    }
}