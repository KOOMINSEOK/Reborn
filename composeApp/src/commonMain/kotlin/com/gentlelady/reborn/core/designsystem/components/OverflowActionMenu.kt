package com.gentlelady.reborn.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.core.theme.RebornDangerRed

/**
 * "⋮" 트리거 + 드롭다운 메뉴로 구성되는 공용 오버플로우 액션 메뉴.
 * 예약된 피드/리마인드, 보관함, 저장(스크랩) 등 "수정/삭제/+상황별 액션" 패턴을 쓰는
 * 관리(management) 하위 화면들이 공통으로 재사용한다.
 */
data class OverflowAction(
    val label: String,
    val isDestructive: Boolean = false,
    val onClick: () -> Unit
)

@Composable
fun OverflowActionMenu(
    actions: List<OverflowAction>,
    modifier: Modifier = Modifier,
    triggerSize: Dp = 28.dp,
    triggerBackgroundColor: Color = Color.White,
    triggerIconTint: Color = Color.Black
) {
    var isExpanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(
            onClick = { isExpanded = true },
            modifier = Modifier
                .size(triggerSize)
                .background(color = triggerBackgroundColor, shape = CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "더보기",
                tint = triggerIconTint,
                modifier = Modifier.size(triggerSize / 1.6f)
            )
        }

        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            actions.forEach { action ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = action.label,
                            color = if (action.isDestructive) RebornDangerRed else Color.Black,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    onClick = {
                        isExpanded = false
                        action.onClick()
                    }
                )
            }
        }
    }
}
