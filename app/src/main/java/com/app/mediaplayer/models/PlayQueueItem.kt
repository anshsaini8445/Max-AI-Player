package com.app.mediaplayer.models

import android.net.Uri

data class PlayQueueItem(
    val mediaItem: MediaItem,
    val position: Int,
    val isPlaying: Boolean = false
)
