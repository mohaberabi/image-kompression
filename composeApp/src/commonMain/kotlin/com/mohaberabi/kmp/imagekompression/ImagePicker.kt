package com.mohaberabi.kmp.imagekompression

import androidx.compose.runtime.Composable


interface ImagePicker {
    suspend fun pickUpImage(): PickedImage?
}


@Composable
expect fun rememberImagePicker(): ImagePicker