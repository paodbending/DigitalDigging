package com.pole.digitaldigging

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.accompanist.appcompattheme.AppCompatTheme
import com.pole.digitaldigging.composescreens.ArtistScreen
import com.pole.digitaldigging.composescreens.searchscreen.SearchScreen
import com.pole.digitaldigging.databinding.ActivityMainBinding
import com.pole.digitaldigging.state.main.MainState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setContent {
            AppCompatTheme {
                when (val state = viewModel.mainState.value) {
                    is MainState.SearchScreen -> {
                        SearchScreen(state.searchScreenState.value)
                    }
                    is MainState.ArtistScreen -> {
                        ArtistScreen(state.stateHolder.value)
                    }
                }
            }
        }
    }
}