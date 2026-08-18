package com.gentlelady.reborn.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.core.theme.RebornDeepBlue
import com.gentlelady.reborn.core.theme.RebornGridBorderGray
import com.gentlelady.reborn.core.theme.RebornIconBoxBlue
import com.gentlelady.reborn.core.theme.RebornSlateGray
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * "프로필 편집", "기본 정보 관리", "공개 범위 및 보안"처럼 여러 선택지를 하나의 둥근 박스 안에
 * 구분선으로 묶어 보여주는 공용 리스트. 각 항목은 [OptionListItem.icon]이 있으면 아이콘 박스를 그리고,
 * 없으면 글자가 바로 앞으로 붙는다. 아이콘은 앱 번들 드로어블/머티리얼 아이콘 둘 다 받을 수 있다.
 */
sealed interface OptionListIcon {
    data class Drawable(val res: DrawableResource) : OptionListIcon
    data class Vector(val imageVector: ImageVector) : OptionListIcon
}

data class OptionListItem(
    val label: String,
    val icon: OptionListIcon? = null,
    val onClick: () -> Unit
)

@Composable
fun GroupedOptionList(
    items: List<OptionListItem>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = Color.White, shape = RoundedCornerShape(16.dp))
            .border(width = 1.dp, color = RebornGridBorderGray, shape = RoundedCornerShape(16.dp))
    ) {
        items.forEachIndexed { index, item ->
            OptionListRow(item = item)
            if (index != items.lastIndex) {
                // 💡 아이콘이 없으면 텍스트 시작 위치(16dp)에 맞춰 좌우 여백을 두고, 있으면 아이콘 뒤 텍스트 위치(64dp)에 맞춘다.
                val dividerInset = if (item.icon != null) 64.dp else 16.dp
                HorizontalDivider(
                    modifier = Modifier.padding(start = dividerInset, end = 16.dp),
                    thickness = 1.dp,
                    color = RebornGridBorderGray
                )
            }
        }
    }
}

@Composable
private fun OptionListRow(
    item: OptionListItem,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { item.onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        item.icon?.let { icon ->
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color = RebornIconBoxBlue, shape = RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                when (icon) {
                    is OptionListIcon.Drawable -> Icon(
                        painter = painterResource(icon.res),
                        contentDescription = item.label,
                        tint = RebornDeepBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    is OptionListIcon.Vector -> Icon(
                        imageVector = icon.imageVector,
                        contentDescription = item.label,
                        tint = RebornDeepBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
        }

        Text(
            text = item.label,
            color = Color.Black,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = RebornSlateGray,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Preview
@Composable
private fun GroupedOptionListNoIconPreview() {
    MaterialTheme {
        Surface {
            GroupedOptionList(
                items = listOf(
                    OptionListItem(label = "기본 정보 관리", onClick = {}),
                    OptionListItem(label = "결제 관리", onClick = {})
                ),
                modifier = Modifier.padding(24.dp)
            )
        }
    }
}

@Preview
@Composable
private fun GroupedOptionListWithIconPreview() {
    MaterialTheme {
        Surface {
            GroupedOptionList(
                items = listOf(
                    OptionListItem(label = "계정 공개 범위", icon = OptionListIcon.Vector(Icons.Default.Lock), onClick = {}),
                    OptionListItem(label = "로그인 기기 관리", icon = OptionListIcon.Vector(Icons.Default.Lock), onClick = {})
                ),
                modifier = Modifier.padding(24.dp)
            )
        }
    }
}
