package com.gentlelady.reborn.core.designsystem.PostCard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.core.theme.*
import com.gentlelady.reborn.home.domain.model.HomePost
import com.gentlelady.reborn.ic_bookmark
import com.gentlelady.reborn.ic_comment
import com.gentlelady.reborn.ic_like
import com.gentlelady.reborn.ic_lock
import com.gentlelady.reborn.ic_share
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun PostHeader(
    post: HomePost,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(36.dp),
            shape = CircleShape,
            color = RebornSurfaceVariant
        ) {
            if (post.authorProfileUrl != null) {
                Image(
                    painter = painterResource(post.authorProfileUrl!!),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(Icons.Default.Person, null, tint = RebornWhite)
            }
        }
        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = post.authorName,
                    color = RebornTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )

                if (post.isPosthumous) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(color = RebornSoftBlue, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_lock),
                            contentDescription = "Memorial Lock",
                            tint = RebornCobaltBlue,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = post.postedAt,
                color = RebornSlateGray,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        Icon(Icons.Default.MoreHoriz, contentDescription = null, tint = RebornUnselectedGray)
    }
}

@Composable
internal fun PosthumousBanner(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(width = 1.dp, color = RebornBorderLightBlue, shape = RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(RebornPosthumousBannerBg)
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = RebornNavSelected,
                modifier = Modifier.size(18.dp).padding(top = 1.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "사후 게시글 — 작성자가 미리 예약해 두고 사망 후 공개되는 게시글입니다.",
                color = RebornNavSelected,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
internal fun PostImageArea(
    post: HomePost,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(top = 4.dp)
            .background(RebornDividerGray)
    ) {
        if (post.contentImageUrl != null) {
            Image(
                painter = painterResource(post.contentImageUrl!!),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = "Image Area",
                modifier = Modifier.align(Alignment.Center),
                color = RebornSlateGray
            )
        }
    }
}

@Composable
internal fun PostActionRow(
    post: HomePost,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ActionItem(
            iconRes = Res.drawable.ic_like,
            count = post.likes.toString(),
            contentDescription = "Like"
        )
        Spacer(modifier = Modifier.width(16.dp))
        ActionItem(
            iconRes = Res.drawable.ic_comment,
            count = post.comments.toString(),
            contentDescription = "Comment"
        )
        Spacer(modifier = Modifier.width(16.dp))
        Icon(
            painter = painterResource(Res.drawable.ic_share),
            contentDescription = "Share",
            tint = RebornActionIconDark,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            painter = painterResource(Res.drawable.ic_bookmark),
            contentDescription = "Bookmark",
            tint = RebornActionIconDark,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun ActionItem(
    iconRes: DrawableResource,
    count: String,
    contentDescription: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            tint = RebornActionIconDark,
            modifier = Modifier.size(24.dp)
        )
        Text(text = " $count", fontSize = 14.sp, color = RebornSlateGray)
    }
}

private const val CaptionCollapsedMaxLines = 2

@Composable
internal fun PostCaption(
    post: HomePost,
    modifier: Modifier = Modifier
) {
    var expanded by remember(post.id) { mutableStateOf(false) }
    var isOverflowing by remember(post.id) { mutableStateOf(false) }
    var cutoffIndex by remember(post.id) { mutableStateOf(-1) }

    val fullCaption = buildAnnotatedString {
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = RebornTextPrimary)) {
            append("@${post.authorName} ")
        }
        append(post.caption)
    }

    val displayedCaption = buildAnnotatedString {
        if (expanded || !isOverflowing || cutoffIndex < 0) {
            append(fullCaption)
        } else {
            append(fullCaption.subSequence(0, cutoffIndex))
            append("… ")
        }
        if (isOverflowing) {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = RebornSlateGray)) {
                append(if (expanded) "간략히 보기" else "더 보기")
            }
        }
    }

    Text(
        text = displayedCaption,
        modifier = modifier
            .padding(horizontal = 16.dp)
            .clickable(enabled = isOverflowing) { expanded = !expanded },
        color = RebornTextPrimary,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        maxLines = if (expanded) Int.MAX_VALUE else CaptionCollapsedMaxLines,
        overflow = TextOverflow.Clip,
        onTextLayout = { layoutResult ->
            if (!expanded && cutoffIndex < 0) {
                if (layoutResult.hasVisualOverflow) {
                    isOverflowing = true
                    val lastLine = CaptionCollapsedMaxLines - 1
                    val lastVisibleCharIndex = layoutResult.getLineEnd(lastLine, visibleEnd = true)
                    // "더 보기" 자리를 확보하기 위해 끝부분을 살짝 잘라낸다.
                    cutoffIndex = (lastVisibleCharIndex - 6).coerceAtLeast(0)
                }
            }
        }
    )
}