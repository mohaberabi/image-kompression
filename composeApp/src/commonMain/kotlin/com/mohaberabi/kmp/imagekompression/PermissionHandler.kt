package com.mohaberabi.kmp.imagekompression

import androidx.compose.runtime.Composable

interface PermissionHandler {
    suspend fun checkPermission(
        permission: AppPermission,
    ): PermissionResult
}


@Composable
expect fun rememberPermissionHandler(): PermissionHandler