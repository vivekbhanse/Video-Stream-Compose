package com.reminder.myottapp.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.exoplayer.ExoPlayer
import com.reminder.myottapp.feature.player.PlayerViewModel

@Composable
fun PlayerLifecycleHandler(viewModel: PlayerViewModel) {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> viewModel.exoPlayer.pause()
                Lifecycle.Event.ON_STOP -> {}
                Lifecycle.Event.ON_DESTROY -> {
//                    viewModel.exoPlayer.release()
                }
                Lifecycle.Event.ON_RESUME -> {
//                    viewModel.restoreState()
                    viewModel.exoPlayer.play()
                }
                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}
