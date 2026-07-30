package com.gentlelady.reborn.core.designsystem.components

import androidx.compose.ui.graphics.ImageBitmap
import org.jetbrains.compose.resources.DrawableResource

/**
 * 그리드/카드 이미지가 앱 번들 리소스(DrawableResource)인지, 갤러리에서 직접 등록한
 * 런타임 비트맵(ImageBitmap)인지를 함께 다루기 위한 공용 타입.
 */
sealed interface GridImageSource {
    data class Resource(val res: DrawableResource) : GridImageSource
    data class Bitmap(val bitmap: ImageBitmap) : GridImageSource
}
