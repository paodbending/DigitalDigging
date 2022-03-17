package com.example.digitaldigging.screens.common.artistinfolist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.digitaldigging.databinding.LayoutArtistBinding
import com.pole.domain.model.ArtistInfo

class ArtistsInfoAdapter(private val onClick: (ArtistInfo) -> Unit) :
    ListAdapter<ArtistInfo, ArtistInfoViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistInfoViewHolder {
        val binding =
            LayoutArtistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArtistInfoViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: ArtistInfoViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    private object DiffCallback : DiffUtil.ItemCallback<ArtistInfo>() {
        override fun areItemsTheSame(oldItem: ArtistInfo, newItem: ArtistInfo): Boolean {
            return oldItem.artist.spotifyId == newItem.artist.spotifyId
        }

        override fun areContentsTheSame(oldItem: ArtistInfo, newItem: ArtistInfo): Boolean {
            return oldItem == newItem
        }
    }
}