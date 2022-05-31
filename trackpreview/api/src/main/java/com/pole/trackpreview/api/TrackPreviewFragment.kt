package com.pole.trackpreview.api

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.pole.trackpreview.api.databinding.FragmentTrackPreviewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackPreviewFragment : Fragment() {

    private val viewModel: TrackPreviewViewModel by viewModels({ requireParentFragment() })

    private var _binding: FragmentTrackPreviewBinding? = null

    private val binding: FragmentTrackPreviewBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTrackPreviewBinding.inflate(layoutInflater, container, false)

        viewModel.trackPreviewPlayer.observe(viewLifecycleOwner) { state ->
            when (state) {
                TrackPreviewState.NotAvailable -> {
                    binding.loadingProgressBar.visibility = View.INVISIBLE
                    binding.previewButton.visibility = View.INVISIBLE
                    binding.previewTextView.visibility = View.VISIBLE
                }
                TrackPreviewState.Loading -> {
                    binding.previewButton.visibility = View.INVISIBLE
                    binding.previewTextView.visibility = View.INVISIBLE
                    binding.loadingProgressBar.visibility = View.VISIBLE
                }
                TrackPreviewState.Ready -> {
                    binding.loadingProgressBar.visibility = View.INVISIBLE
                    binding.previewTextView.visibility = View.INVISIBLE
                    binding.previewButton.visibility = View.VISIBLE
                    binding.previewImageView.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    binding.previewProgressBar.progress = 0
                }
                is TrackPreviewState.Playing -> {
                    binding.loadingProgressBar.visibility = View.INVISIBLE
                    binding.previewTextView.visibility = View.INVISIBLE
                    binding.previewButton.visibility = View.VISIBLE
                    binding.previewImageView.setImageResource(R.drawable.ic_baseline_pause_24)
                    binding.previewProgressBar.progress = state.progress
                }
            }
        }

        binding.previewButton.setOnClickListener {
            val currentState = viewModel.trackPreviewPlayer.value
            if (currentState is TrackPreviewState.Ready) {
                viewModel.trackPreviewPlayer.play()
            } else if (currentState is TrackPreviewState.Playing) {
                viewModel.trackPreviewPlayer.pause()
            }
        }

        return binding.root
    }

    override fun onPause() {
        super.onPause()
        if (viewModel.trackPreviewPlayer.value is TrackPreviewState.Playing)
            viewModel.trackPreviewPlayer.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}