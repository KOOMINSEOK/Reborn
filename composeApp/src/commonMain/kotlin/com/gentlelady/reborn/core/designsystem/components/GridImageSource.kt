package com.gentlelady.reborn.core.designsystem.components

import androidx.compose.ui.graphics.ImageBitmap
import org.jetbrains.compose.resources.DrawableResource

/**
 * 그리드/카드 이미지 소스. 서버 URL / 앱 번들 리소스 / 갤러리 런타임 비트맵을 함께 다룬다.
 */
sealed interface GridImageSource {
    data class Url(val url: String) : GridImageSource
    data class Resource(val res: DrawableResource) : GridImageSource
    data class Bitmap(val bitmap: ImageBitmap) : GridImageSource
}
