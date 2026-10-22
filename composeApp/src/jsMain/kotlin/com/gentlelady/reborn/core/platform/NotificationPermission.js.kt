package com.gentlelady.reborn.core.platform

import androidx.compose.runtime.Composable

// TODO: js 알림 권한 미구현 (프로젝트는 현재 Android 우선으로 진행 중)
@Composable
actual fun rememberNotificationPermissionRequester(onResult: (Boolean) -> Unit): () -> Unit = { onResult(false) }
