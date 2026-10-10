package com.app.mediaplayer.models

import android.net.Uri

data class MediaItem(
    val id: Long,
    val title: String,
    val uri: Uri,
    val isVideo: Boolean,
    val duration: Long = 0,
    val artist: String = "Unknown",
    val album: String = "Unknown",
    val size: Long = 0
)
