package com.gentlelady.reborn.core.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

/**
 * 서버 URL(`url`) 이면 Coil 로, 없으면 로컬 리소스(`fallback`) 로 그린다.
 * 둘 다 null 이면 아무것도 그리지 않는다 — 호출부가 플레이스홀더를 담당.
 */
@Composable
fun RebornImage(
    url: String?,
    fallback: DrawableResource?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
) {
    when {
        !url.isNullOrBlank() -> AsyncImage(
            model = url,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale,
        )
        fallback != null -> Image(
            painter = painterResource(fallback),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale,
        )
        else -> Unit
    }
}

/** url/fallback 중 하나라도 있으면 이미지를 그릴 수 있다. */
fun hasImage(url: String?, fallback: DrawableResource?): Boolean =
    !url.isNullOrBlank() || fallback != null
