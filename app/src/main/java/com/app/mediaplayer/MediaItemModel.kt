package com.app.mediaplayer

import android.net.Uri

data class MediaItemModel(
    val id: Long,
    val title: String,
    val uri: Uri,
    val isVideo: Boolean
)