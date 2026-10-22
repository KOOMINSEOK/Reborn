package com.gentlelady.reborn.core.designsystem.PostCard

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.core.designsystem.components.RebornImage
import com.gentlelady.reborn.core.designsystem.components.RebornImagePager
import com.gentlelady.reborn.core.designsystem.components.hasImage
import com.gentlelady.reborn.core.theme.*
import com.gentlelady.reborn.home.domain.model.HomePost
import com.gentlelady.reborn.ic_bookmark
import com.gentlelady.reborn.ic_comment
import com.gentlelady.reborn.ic_like
import com.gentlelady.reborn.ic_like_filled
import com.gentlelady.reborn.ic_lock
import com.gentlelady.reborn.ic_share
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun PostHeader(
    post: HomePost,
    modifier: Modifier = Modifier,
    onAuthorClick: (() -> Unit)? = null,
    /** 내 글이면 ··· 메뉴에 '보관하기' */
    onArchiveClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 사진·이름을 누르면 작성자 프로필로 (서버 글만 — 목업 글은 작성자 id 가 없다)
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .then(if (onAuthorClick != null) Modifier.clickable(onClick = onAuthorClick) else Modifier),
            verticalAlignment = Alignment.CenterVertically
        ) {
        Box(modifier = Modifier.size(36.dp)) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = CircleShape,
                color = RebornSurfaceVariant
            ) {
                if (hasImage(post.authorAvatarUrl, post.authorProfileUrl)) {
                    RebornImage(
                        url = post.authorAvatarUrl,
                        fallback = post.authorProfileUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(Icons.Default.Person, null, tint = RebornWhite)
                }
            }

            if (post.isPosthumous) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(16.dp)
                        .border(width = 1.5.dp, color = RebornWhite, shape = CircleShape)
                        .background(color = RebornCobaltBlue, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_lock),
                        contentDescription = "Memorial Lock",
                        tint = RebornWhite,
                        modifier = Modifier.size(9.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = post.authorName,
                color = RebornTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = post.postedAt,
                color = RebornSlateGray,
                fontSize = 12.sp
            )
        }
        }

        if (onArchiveClick != null) {
            var open by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
            Box {
                Icon(
                    Icons.Default.MoreHoriz,
                    contentDescription = "더보기",
                    tint = RebornUnselectedGray,
                    modifier = Modifier.clip(CircleShape).clickable { open = true }.padding(4.dp)
                )
                androidx.compose.material3.DropdownMenu(
                    expanded = open,
                    onDismissRequest = { open = false },
                    containerColor = RebornWhite
                ) {
                    androidx.compose.material3.DropdownMenuItem(
                        text = { Text("보관하기") },
                        onClick = {
                            open = false
                            onArchiveClick()
                        }
                    )
                }
            }
        } else {
            Icon(Icons.Default.MoreHoriz, contentDescription = null, tint = RebornUnselectedGray)
        }
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
        if (post.imageUrls.isNotEmpty()) {
            RebornImagePager(urls = post.imageUrls, modifier = Modifier.fillMaxSize())
        } else if (hasImage(null, post.contentImageUrl)) {
            RebornImage(
                url = null,
                fallback = post.contentImageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
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
    modifier: Modifier = Modifier,
    onLikeClick: () -> Unit = {},
    onCommentClick: () -> Unit = {},
    onSaveClick: () -> Unit = {}
) {
    Row(
        modifier = modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ActionItem(
            iconRes = if (post.liked) Res.drawable.ic_like_filled else Res.drawable.ic_like,
            count = post.likes.toString(),
            contentDescription = if (post.liked) "좋아요 취소" else "좋아요",
            tint = if (post.liked) RebornDangerRed else RebornActionIconDark,
            onClick = onLikeClick
        )
        Spacer(modifier = Modifier.width(16.dp))
        ActionItem(
            iconRes = Res.drawable.ic_comment,
            count = post.comments.toString(),
            contentDescription = "댓글",
            onClick = onCommentClick
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
            contentDescription = if (post.saved) "저장 취소" else "저장",
            // 저장한 글은 파란 책갈피
            tint = if (post.saved) RebornCobaltBlue else RebornActionIconDark,
            modifier = Modifier
                .size(24.dp)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(6.dp))
                .clickable(onClick = onSaveClick)
        )
    }
}

@Composable
private fun ActionItem(
    iconRes: DrawableResource,
    count: String,
    contentDescription: String,
    tint: Color = RebornActionIconDark,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.clip(RoundedCornerShape(8.dp)).clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            tint = tint,
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