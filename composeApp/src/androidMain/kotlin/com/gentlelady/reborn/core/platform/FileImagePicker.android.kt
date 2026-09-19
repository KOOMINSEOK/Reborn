package com.gentlelady.reborn.core.platform

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext

// GetContent("image/*")는 PickVisualMedia(사진 보관함 전용 UI)와 달리 다운로드함/드라이브 등
// 일반 문서 제공자를 보여주는 시스템 "파일" 선택 화면을 띄운다.
@Composable
actual fun rememberFileImagePicker(onImagePicked: (ImageBitmap) -> Unit): () -> Unit {
    val context = LocalContext.current
    val latestOnImagePicked by rememberUpdatedState(onImagePicked)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream)?.let { bitmap ->
                    latestOnImagePicked(bitmap.asImageBitmap())
                }
            }
        }
    }

    return {
        launcher.launch("image/*")
    }
}
