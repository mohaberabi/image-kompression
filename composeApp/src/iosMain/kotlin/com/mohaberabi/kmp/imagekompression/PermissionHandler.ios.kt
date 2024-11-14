package com.mohaberabi.kmp.imagekompression

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

class IOsPermissionHandler : PermissionHandler {
    override suspend fun checkPermission(
        permission: AppPermission,
    ): PermissionResult {
        return when (permission) {
            AppPermission.Gallery -> GalleryPermissionHandler().check()
        }
    }
}

@Composable
actual fun rememberPermissionHandler(): PermissionHandler {
    return remember { IOsPermissionHandler() }
}