package com.pole.digitaldigging

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.appcompattheme.AppCompatTheme
import com.pole.digitaldigging.screens.albumscreen.AlbumScreen
import com.pole.digitaldigging.screens.artistscreen.ArtistScreen
import com.pole.digitaldigging.screens.search.SearchScreen
import com.pole.digitaldigging.screens.trackscreen.TrackScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppCompatTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "search") {
                    composable("search") {
                        SearchScreen(
                            onArtistClick = {
                                navController.navigate("artist/$it")
                            },
                            onAlbumClick = {
                                navController.navigate("album/$it")
                            },
                            onTrackClick = {
                                navController.navigate("track/$it")
                            }
                        )
                    }
                    composable(
                        route = "artist/{artistId}",
                        arguments = listOf(navArgument("artistId") { type = NavType.StringType })
                    ) { navBackStackEntry ->
                        ArtistScreen(
                            artistId = navBackStackEntry.arguments?.getString("artistId") ?: "",
                            onAlbumClick = {
                                navController.navigate("album/$it")
                            },
                        )
                    }
                    composable(
                        route = "album/{albumId}",
                        arguments = listOf(navArgument("albumId") { type = NavType.StringType })
                    ) { navBackStackEntry ->
                        AlbumScreen(
                            albumId = navBackStackEntry.arguments?.getString("albumId") ?: "",
                            onArtistClick = {
                                navController.navigate("artist/$it")
                            },
                            onTrackClick = {
                                navController.navigate("track/$it")
                            }
                        )
                    }
                    composable(
                        route = "track/{trackId}",
                        arguments = listOf(navArgument("trackId") { type = NavType.StringType })
                    ) { navBackStackEntry ->
                        TrackScreen(
                            trackId = navBackStackEntry.arguments?.getString("trackId") ?: "",
                            onArtistClick = {
                                navController.navigate("artist/$it")
                            },
                            onAlbumClick = {
                                navController.navigate("album/$it")
                            },
                            onTrackClick = {
                                navController.navigate("track/$it")
                            }
                        )
                    }
                }
            }
        }
    }
}
