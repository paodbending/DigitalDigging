package com.example.digitaldigging.screens.common.albumlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.digitaldigging.databinding.LayoutAlbumBinding
import com.pole.domain.model.spotify.Album

class AlbumAdapter(private val wrap: Boolean = false, private val onClick: (Album) -> Unit) :
    ListAdapter<Album, AlbumViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val binding = LayoutAlbumBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        if(wrap) binding.root.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT

        return AlbumViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.album = currentList[position]
    }

    private object DiffCallback : DiffUtil.ItemCallback<Album>() {
        override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean {
            return oldItem == newItem
        }
    }
}