package com.gentlelady.reborn.core.platform

import androidx.compose.runtime.Composable

/**
 * 알림 권한 요청을 띄우는 함수를 돌려준다. 결과(허용했나)는 [onResult].
 * 권한이 따로 없는 환경(Android 12 이하 등)에선 바로 true.
 */
@Composable
expect fun rememberNotificationPermissionRequester(onResult: (Boolean) -> Unit): () -> Unit
