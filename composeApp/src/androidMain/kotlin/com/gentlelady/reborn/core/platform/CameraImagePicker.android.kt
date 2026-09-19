package com.gentlelady.reborn.core.platform

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

// TakePicturePreview는 별도 FileProvider 설정 없이 카메라 앱을 실행해 썸네일 크기 Bitmap을 바로 돌려준다.
// ACTION_IMAGE_CAPTURE 계열 암시적 인텐트라 android.permission.CAMERA 선언도 필요 없다.
@Composable
actual fun rememberCameraImagePicker(onImagePicked: (ImageBitmap) -> Unit): () -> Unit {
    val latestOnImagePicked by rememberUpdatedState(onImagePicked)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let { latestOnImagePicked(it.asImageBitmap()) }
    }

    return {
        launcher.launch(null)
    }
}
