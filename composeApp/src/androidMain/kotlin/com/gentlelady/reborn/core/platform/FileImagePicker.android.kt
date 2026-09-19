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

// Android 13+에서는 GetContent("image/*")를 시스템이 자동으로 사진 선택기(Photo Picker)로
// 가로채 버려서 "사진 보관함"과 구분이 안 된다. OpenDocument(SAF)를 쓰면 그 리다이렉트 없이
// 다운로드함/드라이브 등 일반 문서 제공자를 보여주는 진짜 "파일" 선택 화면이 뜬다.
@Composable
actual fun rememberFileImagePicker(onImagePicked: (ImageBitmap) -> Unit): () -> Unit {
    val context = LocalContext.current
    val latestOnImagePicked by rememberUpdatedState(onImagePicked)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
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
        launcher.launch(arrayOf("image/*"))
    }
}
