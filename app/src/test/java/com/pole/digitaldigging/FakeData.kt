package com.pole.digitaldigging

import com.pole.domain.entities.*

class FakeData {
    companion object {
        val artist = Artist(
            id = "",
            spotifyUrl = null,
            name = "Eminem",
            type = "",
            imageUrl = null,
            followers = 0,
            genres = emptyList(),
            popularity = 0
        )

        val album = Album(
            id = "",
            spotifyUrl = null,
            albumType = AlbumType.ALBUM,
            name = "Encore",
            type = "",
            artistIds = emptyList(),
            artistNames = emptyList(),
            imageUrl = null,
            totalTracks = null,
            releaseDate = ReleaseDate(0, 0, 0),
            genres = emptyList(),
            label = "",
            popularity = 0
        )
        val track = Track(
            id = "",
            spotifyUrl = null,
            name = "Bad Guy",
            artistIds = emptyList(),
            previewUrl = null,
            type = "",
            trackNumber = 0,
            discNumber = 0,
            explicit = false,
            length = 0,
            popularity = 0,
            imageUrl = null,
            albumId = "",
            artistNames = emptyList()
        )
    }
}