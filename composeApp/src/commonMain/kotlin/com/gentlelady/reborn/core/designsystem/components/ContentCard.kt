package com.gentlelady.reborn.core.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.core.theme.RebornDividerGray
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.core.theme.RebornTextPrimary
import com.gentlelady.reborn.core.theme.RebornUnselectedGray
import com.gentlelady.reborn.ic_flower_plant
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import androidx.compose.foundation.layout.Row
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * 헤더(아바타+이름+부제) + 이미지 + [footerContent]로 구성되는 공용 카드.
 * RebornCard를 배경으로 사용하며, 홈 피드(PostItem)와 메모리얼 히스토리 상세 화면처럼
 * "레이아웃은 같지만 캡션/액션 영역만 다른" 화면들이 공통으로 재사용한다.
 *
 * 헤더 아래 배너([bannerContent], 예: 사후 게시글 안내)와 이름 옆 배지([headerBadge], 예: 자물쇠 아이콘)는
 * 필요한 쪽에서만 채워 넣도록 선택적으로 열어둔다.
 */
@Composable
fun ContentCard(
    authorName: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    authorProfileRes: DrawableResource? = null,
    imageRes: DrawableResource? = null,
    imageBitmap: ImageBitmap? = null,
    showMoreIcon: Boolean = true,
    headerBadge: (@Composable () -> Unit)? = null,
    bannerContent: (@Composable () -> Unit)? = null,
    footerContent: @Composable () -> Unit
) {
    RebornCard(
        modifier = modifier,
        contentPadding = 0.dp
    ) {
        ContentCardHeader(
            authorName = authorName,
            authorProfileRes = authorProfileRes,
            subtitle = subtitle,
            showMoreIcon = showMoreIcon,
            headerBadge = headerBadge,
            modifier = Modifier.padding(top = 12.dp)
        )

        bannerContent?.invoke()

        when {
            imageBitmap != null -> ContentCardImage(imageBitmap = imageBitmap)
            imageRes != null -> ContentCardImage(imageRes = imageRes)
        }

        footerContent()
    }
}

@Composable
private fun ContentCardHeader(
    authorName: String,
    authorProfileRes: DrawableResource?,
    subtitle: String,
    showMoreIcon: Boolean,
    headerBadge: (@Composable () -> Unit)?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleAvatarImage(
            imageRes = authorProfileRes,
            size = 36.dp,
            fallbackText = authorName,
            borderWidth = 0.dp,
            shadowElevation = 0.dp
        )
        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = authorName,
                    color = RebornTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                headerBadge?.let {
                    Spacer(modifier = Modifier.width(6.dp))
                    it()
                }
            }
            Text(
                text = subtitle,
                color = RebornSlateGray,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        if (showMoreIcon) {
            Icon(Icons.Default.MoreHoriz, contentDescription = null, tint = RebornUnselectedGray)
        }
    }
}

@Composable
private fun ContentCardImage(
    imageRes: DrawableResource,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(top = 8.dp)
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().height(250.dp),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun ContentCardImage(
    imageBitmap: ImageBitmap,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(top = 8.dp)
    ) {
        Image(
            bitmap = imageBitmap,
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().height(250.dp),
            contentScale = ContentScale.Crop
        )
    }
}

@Preview
@Composable
private fun ContentCardPreview() {
    MaterialTheme {
        ContentCard(
            authorName = "김첨지",
            subtitle = "2026.02.18",
            imageRes = Res.drawable.ic_flower_plant,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "ContentCard 미리보기용 캡션입니다.",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}
