package com.gentlelady.reborn.core.platform

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Android 13+에서는 GetContent("image/*")를 시스템이 자동으로 사진 선택기(Photo Picker)로
// 가로채 버려서 "사진 보관함"과 구분이 안 된다. OpenDocument(SAF)를 쓰면 그 리다이렉트 없이
// 다운로드함/드라이브 등 일반 문서 제공자를 보여주는 진짜 "파일" 선택 화면이 뜬다.
@Composable
actual fun rememberFileImagePicker(onImagePicked: (ByteArray) -> Unit): () -> Unit {
    val context = LocalContext.current
    val latestOnImagePicked by rememberUpdatedState(onImagePicked)
    val scope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
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
        launcher.launch(arrayOf("image/*"))
    }
}
