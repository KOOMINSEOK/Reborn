// composeApp/src/commonMain/kotlin/com/gentlelady/reborn/feature/memorial/wreath/MemorialWreathContent.kt
package com.gentlelady.reborn.feature.memorial.wreath

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.core.designsystem.components.CircleIconBadge
import com.gentlelady.reborn.core.theme.RebornCobaltBlue
import com.gentlelady.reborn.core.theme.RebornLightBlueBg
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.ic_flower_plant
import com.gentlelady.reborn.memorial.presentation.MemorialWreathItem
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * 온라인 화환 탭 콘텐츠. 화환이 없으면 빈 상태를, 있으면 앨범 그리드 + 구매 버튼(우하단 플로팅)을 보여준다.
 * 그리드 타일은 공용 ImageGridAlbum(사진, 각 셀 각진 사각형)과 달리 둥근 모서리를 가진
 * 텍스트 타일이라 별도 컴포넌트로 구현했다.
 */
@Composable
internal fun MemorialWreathContent(
    items: List<MemorialWreathItem>,
    onPurchaseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        if (items.isEmpty()) {
            MemorialWreathEmptyState(modifier = Modifier.fillMaxSize())
        } else {
            WreathGrid(
                items = items,
                modifier = Modifier.fillMaxSize()
            )
        }

        WreathPurchaseButton(
            onClick = onPurchaseClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }
}

@Composable
private fun MemorialWreathEmptyState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircleIconBadge(
            icon = Res.drawable.ic_flower_plant,
            contentDescription = null,
            size = 64.dp,
            iconSize = 28.dp,
            backgroundColor = RebornLightBlueBg,
            iconTint = RebornCobaltBlue,
            borderWidth = 0.dp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "아직 헌화된 온라인 화환이 없습니다.",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "가장 먼저 추모의 마음을 전해보세요.",
            fontSize = 13.sp,
            color = RebornSlateGray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun WreathGrid(
    items: List<MemorialWreathItem>,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 2.dp),
        modifier = modifier.background(Color.White)
    ) {
        items(items) { item ->
            WreathTile(item = item)
        }
    }
}

@Composable
private fun WreathTile(
    item: MemorialWreathItem,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(20.dp))
            .background(RebornLightBlueBg),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = item.organizationName,
            color = RebornCobaltBlue,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

@Composable
private fun WreathPurchaseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = RebornCobaltBlue,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_flower_plant),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "온라인 화환 구매",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Preview
@Composable
private fun MemorialWreathContentEmptyPreview() {
    MaterialTheme {
        Surface {
            MemorialWreathContent(
                items = emptyList(),
                onPurchaseClick = {},
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview
@Composable
private fun MemorialWreathContentFilledPreview() {
    MaterialTheme {
        Surface {
            MemorialWreathContent(
                items = listOf(
                    MemorialWreathItem("1", "김철수"),
                    MemorialWreathItem("2", "홍길동"),
                    MemorialWreathItem("3", "이영희"),
                    MemorialWreathItem("4", "IBAS"),
                    MemorialWreathItem("5", "인하가족"),
                    MemorialWreathItem("6", "인천가족모임")
                ),
                onPurchaseClick = {},
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
