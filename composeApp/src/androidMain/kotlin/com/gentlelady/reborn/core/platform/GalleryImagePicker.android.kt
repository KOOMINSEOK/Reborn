package com.gentlelady.reborn.core.platform

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
actual fun rememberGalleryImagePicker(onImagePicked: (ByteArray) -> Unit): () -> Unit {
    val context = LocalContext.current
    val latestOnImagePicked by rememberUpdatedState(onImagePicked)
    val scope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            // 디코딩·인코딩은 무거우니 IO 에서. 콜백(상태 갱신)은 메인으로 돌아와서.
            scope.launch {
                val jpeg = withContext(Dispatchers.IO) { context.readJpegForUpload(uri) }
                if (jpeg != null) latestOnImagePicked(jpeg)
                else Toast.makeText(context, "사진을 불러오지 못했어요. 다른 사진을 골라 주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    return {
        launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
}
