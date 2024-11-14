package com.mohaberabi.kmp.imagekompression

import androidx.compose.foundation.Image


enum class ImageType(
    val mime: String
) {
    Png("image/png"),
    Jpeg("image/jpeg"),
    WebP("image/image/webP")
}


fun String.toImageType() = when (this.lowercase()) {
    "image/jpeg", "jpeg", "jpg" -> ImageType.Jpeg
    "image/webp" -> ImageType.WebP
    else -> ImageType.Png
}

