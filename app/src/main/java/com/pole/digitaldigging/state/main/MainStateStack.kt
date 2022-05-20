package com.pole.digitaldigging.state.main

import com.pole.digitaldigging.state.artist.ArtistScreenStateHolderBuilder
import com.pole.digitaldigging.state.search.SearchScreenStateHolderBuilder
import com.pole.digitaldigging.vhs.StateStack
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainStateStack @Inject constructor(
    private val searchScreenStateHolderBuilder: SearchScreenStateHolderBuilder,
    private val artistScreenStateHolderBuilder: ArtistScreenStateHolderBuilder,
) : StateStack<MainState>() {

    override fun getInitialState(): MainState = MainState.SearchScreen(
        searchScreenState = searchScreenStateHolderBuilder.build(onArtistClickDelegate = { artistId ->
            navigateToArtistPage(artistId)
        }),
        navigateToArtistPageDelegate = { artistId ->
            navigateToArtistPage(artistId)
        },
    )

    private fun navigateToArtistPage(artistId: String) {
        navigateTo(
            MainState.ArtistScreen(
                stateHolder = artistScreenStateHolderBuilder.build(
                    artistId = artistId,
                    onBackPressDelegate = {
                        popBackStack()
                    }
                )
            )
        )
    }
}