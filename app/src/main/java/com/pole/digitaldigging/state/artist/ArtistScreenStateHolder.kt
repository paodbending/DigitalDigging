package com.pole.digitaldigging.state.artist

import com.pole.digitaldigging.UIResource
import com.pole.digitaldigging.state.artist.intent.OnBackPressIntent
import com.pole.digitaldigging.vhs.StateHolder
import com.pole.domain.entities.AlbumType
import com.pole.domain.entities.NetworkResource
import com.pole.domain.usecases.GetArtist
import com.pole.domain.usecases.GetArtistAlbums
import kotlinx.coroutines.flow.combine

class ArtistScreenStateHolder(
    // Use Cases (usually Injected)
    getArtist: GetArtist,
    getArtistAlbums: GetArtistAlbums,
    // Parameters (passed down manually)
    artistId: String,
    private val onBackPressDelegate: OnBackPressIntent,
) : StateHolder<ArtistScreenState>() {

    override fun buildInitialState(): ArtistScreenState =
        ArtistScreenState.Loading(onBackPressDelegate)

    init {
        setStateGenerator(
            combine(
                getArtist(artistId),
                getArtistAlbums(artistId),
            ) { artistResource, albumsResource ->
                when (artistResource) {
                    is NetworkResource.Error -> ArtistScreenState.Error(onBackPressDelegate)
                    is NetworkResource.Loading -> ArtistScreenState.Loading(onBackPressDelegate)
                    is NetworkResource.Ready -> ArtistScreenState.Ready(
                        onBackPressDelegate = onBackPressDelegate,
                        artist = artistResource.value,
                        albums = when (albumsResource) {
                            is NetworkResource.Loading -> UIResource.Loading()
                            is NetworkResource.Error -> UIResource.Error(albumsResource.appError)
                            is NetworkResource.Ready -> UIResource.Ready(ArtistScreenState.Ready.Albums(
                                albums = albumsResource.value.filter { it.albumType == AlbumType.ALBUM },
                                singles = albumsResource.value.filter { it.albumType == AlbumType.SINGLE },
                            ))
                        }
                    )
                }
            }
        )
    }
}
