package com.example.digitaldigging.screens.artistscreen

import com.pole.domain.model.UserData
import com.pole.domain.model.spotify.Album
import com.pole.domain.model.spotify.Artist

sealed interface ArtistInfoScreenState

object ArtistNotFound : ArtistInfoScreenState

object Loading : ArtistInfoScreenState

data class Ready(
    val artist: Artist,
    val userData: UserData,
    val albums: List<Album> = emptyList(),
    val singles: List<Album> = emptyList(),
    val appearsOn: List<Album> = emptyList(),
) : ArtistInfoScreenState