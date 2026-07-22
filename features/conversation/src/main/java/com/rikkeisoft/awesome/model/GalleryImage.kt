package com.rikkeisoft.awesome.model

import android.net.Uri

data class GalleryImage(
    val id: Long,
    val uri: Uri,
    val dateAdded: Long
)
