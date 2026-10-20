package com.gentlelady.reborn.core.platform

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

/**
 * 업로드용 JPEG 로 다시 인코딩한다. HEIC·PNG 등 원본 형식과 상관없이 서버 허용 형식(jpeg)으로 맞춘다.
 * ponytail: 원본 해상도 그대로(축소 없음). 10MB 버킷 제한에 걸리거나 업로드가 느리면 긴 변 2048px 축소 추가.
 */
internal fun Bitmap.toJpeg(): ByteArray =
    ByteArrayOutputStream().use { out ->
        compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
        out.toByteArray()
    }

private const val JPEG_QUALITY = 85
