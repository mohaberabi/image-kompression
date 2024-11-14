package com.mohaberabi.kmp.imagekompression

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.get
import kotlinx.cinterop.reinterpret
import org.jetbrains.skia.Image
import platform.Foundation.NSData
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation

actual class ImageUtils(
    private val uiiImage: UIImage?
) {
    @OptIn(ExperimentalForeignApi::class)
    actual fun toByteArray(): ByteArray? {
        return if (uiiImage == null) {
            null
        } else {
            val nsData: NSData? = UIImageJPEGRepresentation(uiiImage, 0.99)
            return nsData?.let { data ->
                data.toByteArray()
            }
        }

    }

    actual fun toImageBitmap(): ImageBitmap? {
        val byteArray = toByteArray()
        return byteArray?.let {
            Image.makeFromEncoded(byteArray).toComposeImageBitmap()
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
internal fun NSData?.toByteArray(): ByteArray? {
    return if (this == null) {
        null
    } else {
        if (bytes == null) {
            null
        } else {
            val cPointer: CPointer<ByteVar> = bytes!!.reinterpret()
            ByteArray(length.toInt()) { index -> cPointer[index].toByte() }
        }
    }
}