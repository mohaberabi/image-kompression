package com.mohaberabi.kmp.imagekompression

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePNGRepresentation

actual class ImageKompressor {
    actual suspend fun compress(
        picked: PickedImage,
        maxSize: Int
    ): Result<PickedImage> {
        return kotlin.runCatching {
            withContext(Dispatchers.Default) {
                ensureActive()
                val bytes = picked.bytes
                val nsData = bytes.toNSData()
                val image = UIImage(nsData)
                var quality = 0.9
                var compressedData: NSData?
                ensureActive()
                do {
                    compressedData = when (picked.type) {
                        ImageType.Png -> UIImagePNGRepresentation(image)
                        ImageType.Jpeg -> UIImageJPEGRepresentation(image, quality)
                        ImageType.WebP -> UIImagePNGRepresentation(image)
                    }
                    quality -= 0.1
                } while (compressedData != null &&
                    compressedData.length > maxSize.toULong() &&
                    quality > 0.1
                )
                val compressedBytes =
                    compressedData.toByteArray() ?: throw Exception("Could not compress image")
                PickedImage(compressedBytes, picked.type)
            }
        }.onFailure {
            if (it is CancellationException) {
                throw it
            }
        }

    }
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
internal fun ByteArray.toNSData(): NSData {
    return this.usePinned { pinned ->
        NSData.create(
            bytes = pinned.addressOf(0),
            length = this.size.toULong()
        )
    }
}

@Composable
actual fun rememberImageKompressor(): ImageKompressor {
    return remember { ImageKompressor() }
}