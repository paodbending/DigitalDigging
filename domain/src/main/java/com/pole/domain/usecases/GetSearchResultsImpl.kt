package com.pole.domain.usecases

import com.pole.domain.Repository
import com.pole.domain.entities.NetworkResource
import com.pole.domain.entities.SearchResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.transformLatest
import javax.inject.Inject
import javax.inject.Singleton

interface GetSearchResults {
    operator fun invoke(query: String): Flow<NetworkResource<SearchResult>>
}

@Singleton
internal class GetSearchResultsImpl @Inject constructor(
    private val repository: Repository,
    private val getArtists: GetArtists,
    private val getAlbums: GetAlbums,
    private val getTracks: GetTracks,
) : GetSearchResults {
    @OptIn(ExperimentalCoroutinesApi::class)
    override operator fun invoke(query: String): Flow<NetworkResource<SearchResult>> {
        return repository.getSearchResultIds(query).transformLatest { searchResults ->
            when (searchResults) {
                is NetworkResource.Loading -> emit(NetworkResource.Loading())
                is NetworkResource.Error -> emit(NetworkResource.Error(searchResults.appError))
                is NetworkResource.Ready -> emitAll(
                    combine(
                        getArtists(searchResults.value.artistsIds.toSet()),
                        getAlbums(searchResults.value.albumsIds.toSet()),
                        getTracks(searchResults.value.tracksIds.toSet())
                    ) { artistsResource, albumsResource, tracksResource ->
                        when {
                            artistsResource is NetworkResource.Ready && albumsResource is NetworkResource.Ready && tracksResource is NetworkResource.Ready -> {
                                val artists =
                                    searchResults.value.artistsIds
                                        .mapIndexed { i, e -> e to i }
                                        .toMap()
                                        .let { indexes ->
                                            artistsResource.value.sortedBy { indexes[it.id] }
                                        }
                                val albums =
                                    searchResults.value.albumsIds
                                        .mapIndexed { i, e -> e to i }
                                        .toMap()
                                        .let { indexes ->
                                            albumsResource.value.sortedBy { indexes[it.id] }
                                        }
                                val tracks =
                                    searchResults.value.tracksIds
                                        .mapIndexed { i, e -> e to i }
                                        .toMap()
                                        .let { indexes ->
                                            tracksResource.value.sortedBy { indexes[it.id] }
                                        }
                                NetworkResource.Ready(
                                    SearchResult(
                                        artists = artists,
                                        albums = albums,
                                        tracks = tracks
                                    )
                                )
                            }
                            artistsResource is NetworkResource.Error -> NetworkResource.Error(
                                artistsResource.appError)
                            albumsResource is NetworkResource.Error -> NetworkResource.Error(
                                albumsResource.appError)
                            tracksResource is NetworkResource.Error -> NetworkResource.Error(
                                tracksResource.appError)
                            else -> NetworkResource.Loading()
                        }
                    }
                )
            }
        }
    }
}