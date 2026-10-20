package com.gentlelady.reborn.core.platform

import androidx.compose.runtime.Composable

/**
 * 카메라 앱을 직접 실행해 촬영한 사진을 받는 플랫폼별 런처.
 * [rememberGalleryImagePicker]와 달리 사진 보관함이 아닌 카메라 캡처 화면이 뜬다.
 */
// 업로드용 JPEG 바이트를 돌려준다. 미리보기는 Coil 이 바이트를 그대로 그린다.
@Composable
expect fun rememberCameraImagePicker(onImagePicked: (ByteArray) -> Unit): () -> Unit
