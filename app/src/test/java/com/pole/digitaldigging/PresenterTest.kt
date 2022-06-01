package com.pole.digitaldigging

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.testing.TestLifecycleOwner
import com.pole.digitaldigging.screens.search.*
import com.pole.domain.AppError
import com.pole.domain.entities.*
import com.pole.domain.usecases.GetSearchResults
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PresenterTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private val coroutineScope = TestScope(dispatcherRule.dispatcher)

    private val testLifecycleOwner = TestLifecycleOwner(
        initialState = Lifecycle.State.RESUMED,
        coroutineDispatcher = dispatcherRule.dispatcher
    )

    private val fakeGetSearchResults = GetSearchResults {
        flow {
            emit(NetworkResource.Ready(
                SearchResult(
                    artists = List(10) { FakeData.artist },
                    albums = List(10) { FakeData.album },
                    tracks = List(10) { FakeData.track },
                )
            ))
        }
    }

    private val model = ModelImpl(
        getSearchResults = fakeGetSearchResults,
        coroutineScope = coroutineScope,
        defaultDispatcher = dispatcherRule.dispatcher
    )

    @Test
    fun `Tests Results`() = runTest {

        val channel = Channel<Unit>(Channel.CONFLATED)

        val mockView = object : MockView() {
            override fun setBestResults(artist: Artist?, albums: List<Album>, tracks: List<Track>) {
                assert(artist == FakeData.artist)
                assert(albums.firstOrNull() == FakeData.album)
                assert(albums.size <= 5)
                assert(tracks.firstOrNull() == FakeData.track)
                assert(tracks.size <= 5)
                channel.trySend(Unit)
            }

            override fun setArtistsResults(artists: List<Artist>) {
                assert(artists.all { it.imageUrl != null && it.followers >= 1000 })
            }
        }

        val presenter = PresenterImpl(
            model = model,
            view = mockView,
            lifecycleOwner = testLifecycleOwner
        )

        presenter.setSearchQuery("Eminem")

        channel.receive()
    }

    @Test
    fun `Test searchQuery`() = runTest {
        val channel = Channel<String>(Channel.CONFLATED)

        val mockView = object : MockView() {
            override fun setSearchQuery(searchQuery: String) {
                channel.trySend(searchQuery)
            }
        }

        val presenter = PresenterImpl(
            model = model,
            view = mockView,
            lifecycleOwner = testLifecycleOwner
        )

        val searchQuery = "Eminem"
        presenter.setSearchQuery(searchQuery)
        assert(channel.receive() == searchQuery)
    }

    @Test
    fun `Test Search Settings`() = runTest {
        val channel = Channel<SearchType>(Channel.CONFLATED)

        val mockView = object : MockView() {

            override fun setSearchType(searchType: SearchType) {
                channel.trySend(searchType)
            }
        }

        val presenter = PresenterImpl(
            model = model,
            view = mockView,
            lifecycleOwner = testLifecycleOwner
        )

        presenter.setSearchType(SearchType.ALL)
        assert(channel.receive() == SearchType.ALL)

        presenter.setSearchType(SearchType.ARTISTS)
        assert(channel.receive() == SearchType.ARTISTS)

        presenter.setSearchType(SearchType.ALBUMS)
        assert(channel.receive() == SearchType.ALBUMS)

        presenter.setSearchType(SearchType.TRACKS)
        assert(channel.receive() == SearchType.TRACKS)
    }

    @Test
    fun `Test Search Sort Type`() = runTest {
        val artistChannel = Channel<ArtistSortType>(Channel.CONFLATED)
        val albumChannel = Channel<AlbumSortType>(Channel.CONFLATED)
        val trackChannel = Channel<TrackSortType>(Channel.CONFLATED)

        val mockView = object : MockView() {
            override fun setArtistSortType(artistSortType: ArtistSortType) {
                artistChannel.trySend(artistSortType)
            }

            override fun setAlbumSortType(albumSortType: AlbumSortType) {
                albumChannel.trySend(albumSortType)
            }

            override fun setTrackSortType(trackSortType: TrackSortType) {
                trackChannel.trySend(trackSortType)
            }
        }

        val presenter = PresenterImpl(
            model = model,
            view = mockView,
            lifecycleOwner = testLifecycleOwner
        )

        presenter.setArtistSortType(ArtistSortType.RELEVANCE)
        assert(artistChannel.receive() == ArtistSortType.RELEVANCE)

        presenter.setArtistSortType(ArtistSortType.FOLLOWERS)
        assert(artistChannel.receive() == ArtistSortType.FOLLOWERS)

        presenter.setArtistSortType(ArtistSortType.POPULARITY)
        assert(artistChannel.receive() == ArtistSortType.POPULARITY)


        presenter.setAlbumSortType(AlbumSortType.RELEVANCE)
        assert(albumChannel.receive() == AlbumSortType.RELEVANCE)

        presenter.setAlbumSortType(AlbumSortType.POPULARITY)
        assert(albumChannel.receive() == AlbumSortType.POPULARITY)

        presenter.setAlbumSortType(AlbumSortType.RELEASE_DATE)
        assert(albumChannel.receive() == AlbumSortType.RELEASE_DATE)


        presenter.setTrackSortType(TrackSortType.RELEVANCE)
        assert(trackChannel.receive() == TrackSortType.RELEVANCE)

        presenter.setTrackSortType(TrackSortType.POPULARITY)
        assert(trackChannel.receive() == TrackSortType.POPULARITY)

        presenter.setTrackSortType(TrackSortType.LENGTH)
        assert(trackChannel.receive() == TrackSortType.LENGTH)
    }

    @Test
    fun `Test Search Message Visibility`() = runTest {
        val channel = Channel<Boolean>(Channel.CONFLATED)

        val mockView = object : MockView() {
            override fun showBestResults() {
                channel.trySend(false)
            }

            override fun showSearchMessage() {
                channel.trySend(true)
            }

            override fun showAlbumsResults() {
                channel.trySend(false)
            }

            override fun showArtistsResults() {
                channel.trySend(false)
            }

            override fun showErrorMessage() {
                channel.trySend(false)
            }

            override fun showProgressBar() {
                channel.trySend(false)
            }

            override fun showTracksResults() {
                channel.trySend(false)
            }
        }

        val presenter = PresenterImpl(
            model = model,
            view = mockView,
            lifecycleOwner = testLifecycleOwner
        )

        presenter.setSearchQuery("")
        assert(channel.receive())

        presenter.setSearchQuery("Eminem")
        assert(channel.receive().not())
    }

    @Test
    fun `Test Progress Message Visibility`() = runTest {
        val channel = Channel<Boolean>(Channel.CONFLATED)

        val mockView = object : MockView() {
            override fun showBestResults() {
                channel.trySend(false)
            }

            override fun showSearchMessage() {
                channel.trySend(false)
            }

            override fun showAlbumsResults() {
                channel.trySend(false)
            }

            override fun showArtistsResults() {
                channel.trySend(false)
            }

            override fun showErrorMessage() {
                channel.trySend(false)
            }

            override fun showProgressBar() {
                channel.trySend(true)
            }

            override fun showTracksResults() {
                channel.trySend(false)
            }
        }

        val presenter = PresenterImpl(
            model = ModelImpl(
                getSearchResults = { flow { emit(NetworkResource.Loading()) } },
                coroutineScope = coroutineScope,
                defaultDispatcher = dispatcherRule.dispatcher
            ),
            view = mockView,
            lifecycleOwner = testLifecycleOwner
        )

        presenter.setSearchQuery("Eminem")
        assert(channel.receive())
    }

    @Test
    fun `Test Error Message Visibility`() = runTest {
        val channel = Channel<Boolean>(Channel.CONFLATED)

        val mockView = object : MockView() {
            override fun showBestResults() {
                channel.trySend(false)
            }

            override fun showSearchMessage() {
                channel.trySend(false)
            }

            override fun showAlbumsResults() {
                channel.trySend(false)
            }

            override fun showArtistsResults() {
                channel.trySend(false)
            }

            override fun showErrorMessage() {
                channel.trySend(true)
            }

            override fun showProgressBar() {
                channel.trySend(false)
            }

            override fun showTracksResults() {
                channel.trySend(false)
            }
        }

        val presenter = PresenterImpl(
            model = ModelImpl(
                getSearchResults = { flow { emit(NetworkResource.Error(AppError.NetworkError)) } },
                coroutineScope = coroutineScope,
                defaultDispatcher = dispatcherRule.dispatcher
            ),
            view = mockView,
            lifecycleOwner = testLifecycleOwner
        )

        presenter.setSearchQuery("Eminem")
        assert(channel.receive())
    }

    @Test
    fun `Test Best Results Visibility`() = runTest {
        val channel = Channel<Boolean>(Channel.CONFLATED)

        val mockView = object : MockView() {
            override fun showBestResults() {
                channel.trySend(true)
            }

            override fun showSearchMessage() {
                channel.trySend(false)
            }

            override fun showAlbumsResults() {
                channel.trySend(false)
            }

            override fun showArtistsResults() {
                channel.trySend(false)
            }

            override fun showErrorMessage() {
                channel.trySend(false)
            }

            override fun showProgressBar() {
                channel.trySend(false)
            }

            override fun showTracksResults() {
                channel.trySend(false)
            }
        }

        val presenter = PresenterImpl(
            model = model,
            view = mockView,
            lifecycleOwner = testLifecycleOwner
        )

        presenter.setSearchQuery("Eminem")
        presenter.setSearchType(SearchType.ALL)
        assert(channel.receive())
    }

    @Test
    fun `Test Artist Results Visibility`() = runTest {
        val channel = Channel<Boolean>(Channel.CONFLATED)

        val mockView = object : MockView() {
            override fun showBestResults() {
                channel.trySend(false)
            }

            override fun showSearchMessage() {
                channel.trySend(false)
            }

            override fun showAlbumsResults() {
                channel.trySend(false)
            }

            override fun showArtistsResults() {
                channel.trySend(true)
            }

            override fun showErrorMessage() {
                channel.trySend(false)
            }

            override fun showProgressBar() {
                channel.trySend(false)
            }

            override fun showTracksResults() {
                channel.trySend(false)
            }
        }

        val presenter = PresenterImpl(
            model = model,
            view = mockView,
            lifecycleOwner = testLifecycleOwner
        )

        presenter.setSearchQuery("Eminem")
        presenter.setSearchType(SearchType.ARTISTS)
        assert(channel.receive())
    }

    @Test
    fun `Test Albums Results Visibility`() = runTest {
        val channel = Channel<Boolean>(Channel.CONFLATED)

        val mockView = object : MockView() {
            override fun showBestResults() {
                channel.trySend(false)
            }

            override fun showSearchMessage() {
                channel.trySend(false)
            }

            override fun showAlbumsResults() {
                channel.trySend(true)
            }

            override fun showArtistsResults() {
                channel.trySend(false)
            }

            override fun showErrorMessage() {
                channel.trySend(false)
            }

            override fun showProgressBar() {
                channel.trySend(false)
            }

            override fun showTracksResults() {
                channel.trySend(false)
            }
        }

        val presenter = PresenterImpl(
            model = model,
            view = mockView,
            lifecycleOwner = testLifecycleOwner
        )

        presenter.setSearchQuery("Eminem")
        presenter.setSearchType(SearchType.ALBUMS)
        assert(channel.receive())
    }

    @Test
    fun `Test Tracks Results Visibility`() = runTest {
        val channel = Channel<Boolean>(Channel.CONFLATED)

        val mockView = object : MockView() {
            override fun showBestResults() {
                channel.trySend(false)
            }

            override fun showSearchMessage() {
                channel.trySend(false)
            }

            override fun showAlbumsResults() {
                channel.trySend(false)
            }

            override fun showArtistsResults() {
                channel.trySend(false)
            }

            override fun showErrorMessage() {
                channel.trySend(false)
            }

            override fun showProgressBar() {
                channel.trySend(false)
            }

            override fun showTracksResults() {
                channel.trySend(true)
            }
        }

        val presenter = PresenterImpl(
            model = model,
            view = mockView,
            lifecycleOwner = testLifecycleOwner
        )

        presenter.setSearchQuery("Eminem")
        presenter.setSearchType(SearchType.TRACKS)
        assert(channel.receive())
    }
}