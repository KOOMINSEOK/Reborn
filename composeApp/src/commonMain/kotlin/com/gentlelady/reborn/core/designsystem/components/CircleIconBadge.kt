package com.gentlelady.reborn.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.core.theme.RebornCobaltBlue
import com.gentlelady.reborn.core.theme.RebornDividerGray
import com.gentlelady.reborn.ic_flower_plant
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * 원형 배경 위에 아이콘을 올리는 배지형 버튼/뱃지.
 * 기본 모양은 상단바 아이콘 버튼(흰 배경 + 옅은 회색 테두리)이고,
 * 메모리얼 마크처럼 강조가 필요한 곳은 backgroundColor/iconTint/borderWidth를 덮어써서 사용한다.
 */
@Composable
fun CircleIconBadge(
    icon: DrawableResource,
    contentDescription: String?,
    size: Dp,
    modifier: Modifier = Modifier,
    iconSize: Dp = size / 2,
    backgroundColor: Color = Color.White,
    iconTint: Color = Color.Black,
    borderWidth: Dp = 1.dp,
    borderColor: Color = RebornDividerGray
) {
    CircleIconBadgeContainer(
        size = size,
        modifier = modifier,
        backgroundColor = backgroundColor,
        borderWidth = borderWidth,
        borderColor = borderColor
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(iconSize)
        )
    }
}

@Composable
fun CircleIconBadge(
    icon: ImageVector,
    contentDescription: String?,
    size: Dp,
    modifier: Modifier = Modifier,
    iconSize: Dp = size / 2,
    backgroundColor: Color = Color.White,
    iconTint: Color = Color.Black,
    borderWidth: Dp = 1.dp,
    borderColor: Color = RebornDividerGray
) {
    CircleIconBadgeContainer(
        size = size,
        modifier = modifier,
        backgroundColor = backgroundColor,
        borderWidth = borderWidth,
        borderColor = borderColor
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(iconSize)
        )
    }
}

@Composable
private fun CircleIconBadgeContainer(
    size: Dp,
    modifier: Modifier,
    backgroundColor: Color,
    borderWidth: Dp,
    borderColor: Color,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .size(size)
            .background(color = backgroundColor, shape = CircleShape)
            .let {
                if (borderWidth > 0.dp) it.border(width = borderWidth, color = borderColor, shape = CircleShape) else it
            },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Preview
@Composable
private fun CircleIconBadgeDefaultPreview() {
    MaterialTheme {
        Surface {
            Row {
                CircleIconBadge(
                    icon = Res.drawable.ic_flower_plant,
                    contentDescription = "기본 아웃라인",
                    size = 36.dp
                )
            }
        }
    }
}

@Preview
@Composable
private fun CircleIconBadgeAccentPreview() {
    MaterialTheme {
        Surface {
            CircleIconBadge(
                icon = Res.drawable.ic_flower_plant,
                contentDescription = "메모리얼 마크",
                size = 36.dp,
                backgroundColor = RebornCobaltBlue,
                iconTint = Color.White,
                borderWidth = 0.dp
            )
        }
    }
}
