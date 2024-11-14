package com.mohaberabi.kmp.imagekompression

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import imagekompression.composeapp.generated.resources.Res
import imagekompression.composeapp.generated.resources.compose_multiplatform
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.decodeToImageBitmap

@OptIn(ExperimentalResourceApi::class)
@Composable
@Preview
fun App() {
    val permissionHandler = rememberPermissionHandler()
    val imagePicker = rememberImagePicker()

    val scope = rememberCoroutineScope()
    var pickedImage by remember {
        mutableStateOf<PickedImage?>(null)
    }
    var imageType by remember { mutableStateOf<ImageType?>(null) }
    var compressedImage by remember {
        mutableStateOf<PickedImage?>(null)
    }
    val imageKompressor = rememberImageKompressor()
    var compressing by remember { mutableStateOf(false) }


    val hostState = SnackbarHostState()

    MaterialTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState) }
        ) { padding ->
            Column(
                Modifier.fillMaxSize().padding(padding).padding(20.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                pickedImage?.let {
                    Text("Picked Image")
                    Image(
                        bitmap = it.bytes.decodeToImageBitmap(),
                        "", modifier =
                        Modifier.height(200.dp).fillMaxWidth()
                    )
                }

                Spacer(Modifier.height(20.dp))
                compressedImage?.let {
                    Text("Compressed  Image")
                    Image(
                        bitmap = it.bytes.decodeToImageBitmap(),
                        "", modifier =
                        Modifier.height(200.dp).fillMaxWidth()
                    )
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        scope.launch {
                            val result = permissionHandler.checkPermission(AppPermission.Gallery)
                            if (result == PermissionResult.Granted) {
                                val image = imagePicker.pickUpImage()
                                image?.let {
                                    pickedImage = it
                                    imageType = it.type
                                }
                            }
                        }
                    },
                ) {
                    Text("Pickup Image")
                }
                
                pickedImage?.let { picked ->
                    if (compressing) {
                        CircularProgressIndicator()
                    } else {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                scope.launch {
                                    compressing = true
                                    val result = imageKompressor.compress(
                                        picked,
                                        512 * 1024
                                    )
                                    result.onFailure {
                                        compressing = false
                                        hostState.showSnackbar(it.toString())
                                    }.onSuccess {
                                        compressedImage = it
                                        compressing = false
                                    }
                                }
                            },
                        ) {
                            Text("Compress Image")
                        }
                    }

                }

            }

        }


    }
}