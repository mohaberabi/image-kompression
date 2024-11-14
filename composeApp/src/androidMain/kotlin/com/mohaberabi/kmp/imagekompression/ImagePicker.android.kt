package com.mohaberabi.kmp.imagekompression


import androidx.activity.result.contract.ActivityResultContracts

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


@Composable
actual fun rememberImagePicker(): ImagePicker {
    var pickedCallback: ((Uri?) -> Unit)? = remember {
        null
    }
    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
        ) { uri ->
            pickedCallback?.invoke(uri)
        }
    val picker = object : ImagePicker {
        override suspend fun pickUpImage(): PickedImage? {
            return withContext(Dispatchers.IO) {
                suspendCoroutine { continuation ->
                    launcher.launch(
                        PickVisualMediaRequest(
                            mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly,
                        )
                    )
                    pickedCallback = { uri ->
                        if (uri != null) {
                            val bytes = uriToByteArray(context, uri)
                            bytes?.let {
                                continuation.resume(PickedImage(it, uri.getMimeType(context)))
                            } ?: run { continuation.resume(null) }
                        } else {
                            continuation.resume(null)
                        }
                    }
                }
            }
        }
    }
    return remember { picker }
}

fun Uri.getMimeType(context: Context): ImageType =
    context.contentResolver.getType(this)?.toImageType() ?: ImageType.Jpeg

fun uriToByteArray(
    context: Context,
    uri: Uri,
): ByteArray? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        inputStream?.readBytes()
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}