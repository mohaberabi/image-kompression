package com.mohaberabi.kmp.imagekompression

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import kotlin.math.roundToInt

actual class ImageKompressor {
    actual suspend fun compress(
        picked: PickedImage,
        maxSize: Int,
    ): Result<PickedImage> {
        return kotlin.runCatching {
            withContext(Dispatchers.Default) {
                val bytes = picked.bytes
                ensureActive()
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                ensureActive()
                val format = getCompressFormat(picked.type)
                var compressedBytes: ByteArray
                var quality = 90
                do {
                    ByteArrayOutputStream().use { stream ->
                        bitmap.compress(format, quality, stream)
                        compressedBytes = stream.toByteArray()
                        quality -= 10
                    }
                } while (isActive &&
                    compressedBytes.size > maxSize &&
                    format != Bitmap.CompressFormat.PNG &&
                    quality > 10
                )
                PickedImage(compressedBytes, picked.type)
            }
        }.onFailure {
            if (it is CancellationException) {
                throw it
            }
        }

    }

    private fun getCompressFormat(type: ImageType) = when (type) {
        ImageType.Png -> Bitmap.CompressFormat.PNG
        ImageType.Jpeg -> Bitmap.CompressFormat.JPEG
        ImageType.WebP -> if (Build.VERSION.SDK_INT >= 30) {
            Bitmap.CompressFormat.WEBP_LOSSLESS
        } else Bitmap.CompressFormat.WEBP
    }
}

@Composable
actual fun rememberImageKompressor(): ImageKompressor {
    return remember {
        ImageKompressor()
    }
}