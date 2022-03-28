package com.pole.data.databases.spotifycache

import com.adamratzman.spotify.models.*
import com.pole.data.databases.spotifycache.album.CachedAlbum
import com.pole.data.databases.spotifycache.artist.CachedArtist
import com.pole.data.databases.spotifycache.track.CachedTrack

internal fun Artist.toCachedArtist(): CachedArtist {
    return CachedArtist(
        id = id,
        spotifyUrl = externalUrls.spotify,
        name = name,
        type = type,
        imageUrl = images.firstOrNull()?.url,
        followers = followers.total,
        genres = genres.joinToString(separator = ","),
        popularity = popularity
    )
}


internal fun Album.toCachedAlbum(): CachedAlbum {
    return CachedAlbum(
        id = id,
        spotifyUrl = externalUrls.spotify,
        albumType = albumType.name,
        name = name,
        type = type,
        imageUrl = images.firstOrNull()?.url,
        totalTracks = totalTracks,
        releaseDateYear = releaseDate.year,
        releaseDateMonth = releaseDate.month,
        releaseDateDay = releaseDate.day,
        genres = genres.joinToString(separator = ","),
        label = label,
        popularity = popularity
    )
}

internal fun Track.toCachedTrack(): CachedTrack {
    return CachedTrack(
        id = id,
        spotifyUrl = externalUrls.spotify,
        name = name,
        artistIds = artists.joinToString(separator = ",") { it.id },
        previewUrl = previewUrl,
        type = type,
        trackNumber = trackNumber,
        discNumber = discNumber,
        explicit = explicit,
        length = length,
        popularity = popularity,
        albumId = album.id
    )
}

internal fun SimpleTrack.toCachedTrack(albumId: String): CachedTrack {
    return CachedTrack(
        id = id,
        spotifyUrl = externalUrls.spotify,
        name = name,
        artistIds = artists.joinToString(separator = ",") { it.id },
        previewUrl = previewUrl,
        type = type,
        trackNumber = trackNumber,
        discNumber = discNumber,
        explicit = explicit,
        length = length,
        popularity = popularity,
        albumId = albumId
    )
}