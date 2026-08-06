package com.gentlelady.reborn.core.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.core.theme.RebornDividerGray
import com.gentlelady.reborn.core.theme.RebornSlateGray
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * 흰 테두리를 두른 원형 프로필 이미지. 이미지가 없으면 이름 첫 글자를 대체 표시.
 * 여러 화면(메모리얼, 프로필, 피드 등)에서 크기만 바꿔 공용으로 사용할 수 있도록 설계.
 *
 * - [imageUrl]: 서버에 업로드된 프로필 사진 URL. 실제 비동기 로딩/캐싱은 아직 미구현이며,
 *   추후 Coil3 등 KMP 비동기 이미지 로더를 도입하면 이 값으로 원격 이미지를 렌더링할 예정.
 * - [imageRes]: 앱 번들에 포함된 정적 프리뷰/목업용 리소스 (imageUrl이 없을 때 사용)
 * - 둘 다 없으면 [fallbackText] 첫 글자를 표시
 */
@Composable
fun CircleAvatarImage(
    size: Dp,
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    imageRes: DrawableResource? = null,
    fallbackText: String = "",
    borderWidth: Dp = 3.dp,
    borderColor: Color = Color.White,
    backgroundColor: Color = RebornDividerGray,
    shadowElevation: Dp = 6.dp,
    contentDescription: String? = "Profile Picture"
) {
    Surface(
        modifier = modifier
            .size(size)
            .shadow(elevation = shadowElevation, shape = CircleShape, clip = false)
            .border(width = borderWidth, color = borderColor, shape = CircleShape),
        shape = CircleShape,
        color = backgroundColor
    ) {
        Box(contentAlignment = Alignment.Center) {
            when {
                // TODO: imageUrl 비동기 로딩 미구현. Coil3 등 도입 시 AsyncImage로 교체 예정.
                imageRes != null -> {
                    Image(
                        painter = painterResource(imageRes),
                        contentDescription = contentDescription,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
                else -> {
                    Text(
                        text = fallbackText.take(1),
                        fontSize = (size.value / 3).sp,
                        fontWeight = FontWeight.Bold,
                        color = RebornSlateGray
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun CircleAvatarImagePreview() {
    MaterialTheme {
        Surface {
            CircleAvatarImage(
                imageRes = null,
                size = 80.dp,
                fallbackText = "김첨지"
            )
        }
    }
}
