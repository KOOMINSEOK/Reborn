package com.gentlelady.reborn.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.core.theme.RebornBackgroundGray
import com.gentlelady.reborn.core.theme.RebornCobaltBlue
import com.gentlelady.reborn.core.theme.RebornSlateGray
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * 검색/사용자 검색/팔로워 검색 등 여러 화면(검색, 메시지, 피드 작성)에서 반복되던
 * "돋보기 아이콘 + 입력창 + 지우기 버튼" 캡슐형 검색창을 하나로 통일한 컴포넌트.
 */
@Composable
fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "검색",
    backgroundColor: Color = RebornBackgroundGray,
    iconTint: Color = RebornCobaltBlue,
    showClearButton: Boolean = true,
    height: Dp = 44.dp,
    border: BorderStroke? = null
) {
    Row(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(height / 2))
            .background(backgroundColor)
            .then(if (border != null) Modifier.border(border, RoundedCornerShape(height / 2)) else Modifier)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            if (query.isEmpty()) {
                Text(text = placeholder, fontSize = 14.sp, color = RebornSlateGray)
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = TextStyle(fontSize = 14.sp, color = Color.Black),
                cursorBrush = SolidColor(RebornCobaltBlue),
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (showClearButton && query.isNotEmpty()) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "지우기",
                tint = RebornSlateGray,
                modifier = Modifier.size(18.dp).clickable { onQueryChange("") }
            )
        }
    }
}

@Preview
@Composable
private fun SearchFieldPreview() {
    var query by remember { mutableStateOf("") }
    MaterialTheme {
        Surface {
            SearchField(
                query = query,
                onQueryChange = { query = it },
                placeholder = "이름 혹은 ID 검색",
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )
        }
    }
}
