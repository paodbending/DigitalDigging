package com.pole.data.databases.spotifycache

import com.pole.data.databases.spotifycache.album.CachedAlbum
import com.pole.data.databases.spotifycache.artist.CachedArtist
import com.pole.data.databases.spotifycache.track.CachedTrack
import com.pole.domain.entities.*


internal fun CachedArtist.toModelArtist(): Artist {
    return Artist(
        id = id,
        spotifyUrl = spotifyUrl,
        name = name,
        type = type,
        imageUrl = imageUrl,
        followers = followers ?: 0,
        genres = genres.split(","),
        popularity = popularity
    )
}

internal fun CachedAlbum.toModelAlbum(): Album {
    return Album(
        id = id,
        spotifyUrl = spotifyUrl,
        albumType = try {
            AlbumType.valueOf(albumType)
        } catch (e: IllegalArgumentException) {
            AlbumType.UNKNOWN
        },
        name = name,
        type = type,
        artistIds = artistIds.split(","),
        artistNames = artistNames.split(","),
        imageUrl = imageUrl,
        totalTracks = totalTracks,
        genres = genres.split(","),
        label = label,
        popularity = popularity,
        releaseDate = ReleaseDate(
            year = releaseDateYear,
            month = releaseDateMonth,
            day = releaseDateDay
        ),
    )
}

internal fun CachedTrack.toModelTrack(): Track {
    return Track(
        id = id,
        spotifyUrl = spotifyUrl,
        name = name,
        artistIds = artistIds.split(","),
        previewUrl = previewUrl,
        type = type,
        trackNumber = trackNumber,
        discNumber = discNumber,
        explicit = explicit,
        length = length,
        popularity = popularity,
        albumId = albumId,
        imageUrl = imageUrl,
        artistNames = artistNames.split(",")
    )
}