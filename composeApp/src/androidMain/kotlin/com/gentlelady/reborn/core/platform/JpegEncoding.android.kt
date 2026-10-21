package com.gentlelady.reborn.core.platform

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.util.Log
import java.io.ByteArrayOutputStream
import kotlin.math.max

/**
 * 고른 사진을 업로드용 JPEG 로 만든다. 메인 스레드에서 부르지 말 것 (디코딩·인코딩이 무겁다).
 * - 긴 변 [MAX_SIDE]px 로 줄여서 디코딩 → 48MP 원본도 메모리에 통째로 올리지 않는다
 * - EXIF 회전을 반영 (BitmapFactory 는 무시한다)
 * - HEIC·PNG 등 원본 형식과 상관없이 서버 허용 형식(jpeg)으로 맞춘다
 * 읽을 수 없는 파일이면 null — 목록엔 있는데 실제 파일이 지워진 사진, 클라우드에만 있는 사진,
 * 권한이 빠진 Uri, 깨진 이미지 등. 예외를 던지지 않는다 (호출부가 IO 코루틴이라 던지면 앱이 죽는다).
 */
internal fun Context.readJpegForUpload(uri: Uri): ByteArray? = try {
    decodeForUpload(uri)
} catch (e: Exception) {
    Log.w("ImagePicker", "cannot read picked image", e)
    null
}

private fun Context.decodeForUpload(uri: Uri): ByteArray? {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
    val longSide = max(bounds.outWidth, bounds.outHeight)
    if (longSide <= 0) return null

    var sampleSize = 1
    while (longSide / (sampleSize * 2) >= MAX_SIDE) sampleSize *= 2
    val sampled = contentResolver.openInputStream(uri)?.use {
        BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = sampleSize })
    } ?: return null

    val degrees = contentResolver.openInputStream(uri)?.use { exifRotationDegrees(ExifInterface(it)) } ?: 0
    return sampled.fitAndRotate(degrees).toJpeg()
}

/** 카메라 미리보기 등 이미 메모리에 있는 비트맵을 업로드용 JPEG 로. */
internal fun Bitmap.toJpeg(): ByteArray =
    ByteArrayOutputStream().use { out ->
        compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
        out.toByteArray()
    }

/** inSampleSize 는 2의 거듭제곱이라 최대 2배 크게 남을 수 있다 → 정확히 [MAX_SIDE] 로 맞추고 회전. */
private fun Bitmap.fitAndRotate(degrees: Int): Bitmap {
    val scale = MAX_SIDE.toFloat() / max(width, height)
    if (scale >= 1f && degrees == 0) return this
    val matrix = Matrix().apply {
        if (scale < 1f) postScale(scale, scale)
        if (degrees != 0) postRotate(degrees.toFloat())
    }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true).also { if (it !== this) recycle() }
}

private fun exifRotationDegrees(exif: ExifInterface): Int =
    when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90
        ExifInterface.ORIENTATION_ROTATE_180 -> 180
        ExifInterface.ORIENTATION_ROTATE_270 -> 270
        else -> 0
    }

private const val MAX_SIDE = 2048
private const val JPEG_QUALITY = 85
