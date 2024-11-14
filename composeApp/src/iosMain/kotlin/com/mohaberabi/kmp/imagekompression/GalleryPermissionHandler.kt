package com.mohaberabi.kmp.imagekompression

import platform.Photos.PHAuthorizationStatusAuthorized
import platform.Photos.PHAuthorizationStatusLimited
import platform.Photos.PHAuthorizationStatusNotDetermined
import platform.Photos.PHPhotoLibrary
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class GalleryPermissionHandler {
    suspend fun check(): PermissionResult = suspendCoroutine { continuation ->
        val status = PHPhotoLibrary.authorizationStatus()
        when (status) {
            PHAuthorizationStatusAuthorized -> {
                continuation.resume(PermissionResult.Granted)
            }

            PHAuthorizationStatusNotDetermined -> {
                PHPhotoLibrary.requestAuthorization { newStatus ->
                    val result =
                        if (newStatus == PHAuthorizationStatusAuthorized) PermissionResult.Granted
                        else PermissionResult.NotGranted
                    continuation.resume(result)
                }
            }

            PHAuthorizationStatusLimited -> {
                continuation.resume(PermissionResult.Granted)
            }

            else -> {
                continuation.resume(PermissionResult.NotGranted)
            }
        }
    }

}