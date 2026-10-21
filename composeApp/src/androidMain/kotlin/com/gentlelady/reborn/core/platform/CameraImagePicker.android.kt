package com.gentlelady.reborn.core.platform

import android.content.ActivityNotFoundException
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext

// TakePicturePreview는 별도 FileProvider 설정 없이 카메라 앱을 실행해 썸네일 크기 Bitmap을 바로 돌려준다.
// ACTION_IMAGE_CAPTURE 계열 암시적 인텐트라 android.permission.CAMERA 선언도 필요 없다.
@Composable
actual fun rememberCameraImagePicker(onImagePicked: (ByteArray) -> Unit): () -> Unit {
    val context = LocalContext.current
    val latestOnImagePicked by rememberUpdatedState(onImagePicked)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let { latestOnImagePicked(it.toJpeg()) }
    }

    return {
        // 카메라 앱이 없는 기기(일부 태블릿·에뮬레이터)에선 던진다
        try {
            launcher.launch(null)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "카메라를 열 수 없어요.", Toast.LENGTH_SHORT).show()
        }
    }
}
