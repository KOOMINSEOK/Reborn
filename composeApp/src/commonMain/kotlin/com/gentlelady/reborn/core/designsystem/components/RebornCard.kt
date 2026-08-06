package com.gentlelady.reborn.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gentlelady.reborn.core.theme.RebornGridBorderGray
import com.gentlelady.reborn.core.theme.RebornSurface
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * 앱 전반에서 재사용하는 둥근 카드형 컨테이너.
 * 온보딩 목업뿐 아니라 다른 화면의 콘텐츠 박스로도 사용 가능하도록 범용으로 둔다.
 */
@Composable
fun RebornCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    contentPadding: Dp = 16.dp,
    backgroundColor: Color = RebornSurface,
    borderColor: Color = RebornGridBorderGray,
    borderWidth: Dp = 1.dp,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
            .border(
                border = BorderStroke(borderWidth, borderColor),
                shape = RoundedCornerShape(cornerRadius)
            )
            .padding(contentPadding)
    ) {
        content()
    }
}

@Preview
@Composable
private fun RebornCardPreview() {
    MaterialTheme {
        RebornCard(modifier = Modifier.padding(16.dp)) {
            Text(text = "RebornCard 안에 어떤 콘텐츠든 들어갈 수 있습니다.")
        }
    }
}
