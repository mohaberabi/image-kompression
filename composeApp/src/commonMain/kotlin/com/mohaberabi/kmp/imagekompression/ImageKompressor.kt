package com.mohaberabi.kmp.imagekompression

import androidx.compose.runtime.Composable


expect class ImageKompressor {
    suspend fun compress(
        picked: PickedImage,
        maxSize: Int,
    ): Result<PickedImage>
}


@Composable

expect fun rememberImageKompressor(): ImageKompressor