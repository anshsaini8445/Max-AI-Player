package com.app.mediaplayer.models

import android.net.Uri

data class BeatsItem(
    val id: Long,
    val title: String,
    val uri: Uri,
    val bpm: Int = 0,
    val category: String = "General"
)
