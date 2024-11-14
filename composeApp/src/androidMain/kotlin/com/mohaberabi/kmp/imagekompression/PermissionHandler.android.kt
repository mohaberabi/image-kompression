package com.mohaberabi.kmp.imagekompression

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


@Composable
actual fun rememberPermissionHandler(): PermissionHandler {
    val handler = object : PermissionHandler {
        override suspend fun checkPermission(
            permission: AppPermission,
        ): PermissionResult {
            return when (permission) {
                AppPermission.Gallery -> PermissionResult.Granted
            }
        }
    }
    return remember {
        handler
    }
}

internal fun Context.permissionGranted(permission: String): Boolean =
    ContextCompat.checkSelfPermission(
        this,
        permission
    ) == PackageManager.PERMISSION_GRANTED


internal fun AppPermission.toAndroid(): String = when (this) {
    AppPermission.Gallery -> ""
}

