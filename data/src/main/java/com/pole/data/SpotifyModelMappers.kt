package com.pole.data

import com.adamratzman.spotify.models.AlbumResultType
import com.adamratzman.spotify.models.SimpleArtist
import com.adamratzman.spotify.models.SpotifyImage
import com.pole.domain.model.*


internal fun AlbumResultType.toAlbumType(): AlbumType {
    return when (this) {
        AlbumResultType.ALBUM -> AlbumType.ALBUM
        AlbumResultType.SINGLE -> AlbumType.SINGLE
        AlbumResultType.COMPILATION -> AlbumType.COMPILATION
        AlbumResultType.APPEARS_ON -> AlbumType.APPEARS_ON
    }
}

internal fun com.adamratzman.spotify.models.Artist.toArtistInfo(): ArtistInfo {
    return ArtistInfo(
        artist = Artist(
            spotifyId = id,
            spotifyUrl = externalUrls.spotify,
            name = name,
            type = type
        ),
        image = images.firstOrNull()?.toImage(),
        followers = followers.total,
        genres = genres,
        popularity = popularity
    )
}

internal fun com.adamratzman.spotify.models.SimpleAlbum.toAlbum(): Album {
    return Album(
        spotifyId = id,
        spotifyUrl = externalUrls.spotify,
        albumType = albumType.toAlbumType(),
        name = name,
        type = type,
        artists = artists.map { it.toArtist() },
        image = images.firstOrNull()?.toImage(),
        totalTracks = totalTracks
    )
}

internal fun com.adamratzman.spotify.models.Album.toAlbumInfo(): AlbumInfo {
    return AlbumInfo(
        album = Album(
            spotifyId = id,
            spotifyUrl = externalUrls.spotify,
            albumType = albumType.toAlbumType(),
            name = name,
            type = type,
            artists = artists.map { it.toArtist() },
            image = images.firstOrNull()?.toImage(),
            totalTracks = totalTracks
        ),
        releaseDate = releaseDate.toReleaseDate(),
        genres = genres,
        label = label,
        popularity = popularity,
        tracks = tracks.filterNotNull().map { it.toTrack() }
    )
}

internal fun SimpleArtist.toArtist(): Artist {
    return Artist(
        spotifyId = id,
        spotifyUrl = externalUrls.spotify,
        name = name,
        type = type
    )
}


internal fun SpotifyImage.toImage(): Image {
    return Image(
        url = url
    )
}

internal fun com.adamratzman.spotify.models.ReleaseDate.toReleaseDate(): ReleaseDate {
    return ReleaseDate(
        year = year,
        month = month,
        day = day
    )
}

internal fun com.adamratzman.spotify.models.SimpleTrack.toTrack(): Track {
    return Track(
        spotifyId = id,
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
            spotifyId = id,
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