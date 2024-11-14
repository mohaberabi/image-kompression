package com.mohaberabi.kmp.imagekompression

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.ByteArrayOutputStream

actual class ImageUtils(
    private val bitmap: Bitmap?
) {
    actual fun toByteArray(): ByteArray? {

        return if (bitmap == null) {
            null
        } else {
            val output = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output)
            output.toByteArray()
        }

    }

    actual fun toImageBitmap(): ImageBitmap? = toByteArray()?.let {
        BitmapFactory.decodeByteArray(it, 0, it.size)
    }?.asImageBitmap()


}