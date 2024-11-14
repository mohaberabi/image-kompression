package com.mohaberabi.kmp.imagekompression

import androidx.compose.ui.graphics.ImageBitmap

expect class ImageUtils {


    fun toByteArray(): ByteArray?

    fun toImageBitmap(): ImageBitmap?

}