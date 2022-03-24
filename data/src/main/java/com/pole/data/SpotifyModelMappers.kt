package com.pole.data

import com.adamratzman.spotify.models.AlbumResultType
import com.adamratzman.spotify.models.SimpleArtist
import com.pole.domain.model.*


internal fun AlbumResultType.toAlbumType(): AlbumType {
    return when (this) {
        AlbumResultType.ALBUM -> AlbumType.ALBUM
        AlbumResultType.SINGLE -> AlbumType.SINGLE
        AlbumResultType.COMPILATION -> AlbumType.COMPILATION
        AlbumResultType.APPEARS_ON -> AlbumType.APPEARS_ON
    }
}

internal fun com.adamratzman.spotify.models.ReleaseDate.toReleaseDate(): ReleaseDate {
    return ReleaseDate(
        year = year,
        month = month,
        day = day
    )
}

internal fun com.adamratzman.spotify.models.Artist.toArtistInfo(): ArtistInfo {
    return ArtistInfo(
        artist = Artist(
            id = id,
            spotifyUrl = externalUrls.spotify,
            name = name,
            type = type
        ),
        imageUrl = images.firstOrNull()?.url,
        followers = followers.total,
        genres = genres,
        popularity = popularity
    )
}

internal fun com.adamratzman.spotify.models.SimpleAlbum.toAlbum(): Album {
    return Album(
        id = id,
        spotifyUrl = externalUrls.spotify,
        albumType = albumType.toAlbumType(),
        name = name,
        type = type,
        artists = artists.map { it.toArtist() },
        imageUrl = images.firstOrNull()?.url,
        totalTracks = totalTracks
    )
}

internal fun SimpleArtist.toArtist(): Artist {
    return Artist(
        id = id,
        spotifyUrl = externalUrls.spotify,
        name = name,
        type = type
    )
}

internal fun com.adamratzman.spotify.models.Album.toAlbumInfo(): AlbumInfo {
    return AlbumInfo(
        album = Album(
            id = id,
            spotifyUrl = externalUrls.spotify,
            albumType = albumType.toAlbumType(),
            name = name,
            type = type,
            artists = artists.map { it.toArtist() },
            imageUrl = images.firstOrNull()?.url,
            totalTracks = totalTracks
        ),
        releaseDate = releaseDate.toReleaseDate(),
        genres = genres,
        label = label,
        popularity = popularity,
        tracks = tracks.mapNotNull { it?.toTrack() }
    )
}

internal fun com.adamratzman.spotify.models.SimpleTrack.toTrack(): Track {
    return Track(
        id = id,
        spotifyUrl = externalUrls.spotify,
        name = name,
        artists = artists.map { it.toArtist() },
        previewUrl = previewUrl,
        type = type,
        trackNumber = trackNumber,
        discNumber = discNumber,
        length = length,
        explicit = explicit,
        popularity = popularity
    )
}

internal fun com.adamratzman.spotify.models.Track.toTrackInfo(): TrackInfo {
    return TrackInfo(
        track = Track(
            id = id,
            spotifyUrl = externalUrls.spotify,
            name = name,
            artists = artists.map { it.toArtist() },
            previewUrl = previewUrl,
            type = type,
            trackNumber = trackNumber,
            discNumber = discNumber,
            length = length,
            explicit = explicit,
            popularity = popularity
        ),
        album = album.toAlbum()
    )
}