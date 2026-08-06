// composeApp/src/commonMain/kotlin/com/gentlelady/reborn/feature/wreath_purchase/components/WreathMessageFields.kt
package com.gentlelady.reborn.feature.wreath_purchase.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.core.theme.RebornBackgroundGray
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.core.theme.RebornTopBarDividerGray

/** 추모 텍스트 입력 박스. 우하단에 "N / max" 글자 수 카운터를 함께 표시한다. */
@Composable
fun WreathMessageTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    maxLength: Int,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 96.dp)
            .background(RebornBackgroundGray, RoundedCornerShape(12.dp))
            .border(1.dp, RebornTopBarDividerGray, RoundedCornerShape(12.dp))
    ) {
        BasicTextField(
            value = value,
            onValueChange = { if (it.length <= maxLength) onValueChange(it) },
            textStyle = TextStyle(fontSize = 14.sp, color = Color.Black, lineHeight = 20.sp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, start = 12.dp, end = 12.dp, bottom = 28.dp),
            decorationBox = { innerField ->
                if (value.isEmpty()) {
                    Text(text = placeholder, fontSize = 14.sp, color = RebornSlateGray)
                }
                innerField()
            }
        )
        Text(
            text = "${value.length} / $maxLength",
            fontSize = 12.sp,
            color = RebornSlateGray,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 10.dp)
        )
    }
}

/** "보내는 이" 한 줄 입력 박스. */
@Composable
fun WreathSenderNameField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(RebornBackgroundGray, RoundedCornerShape(12.dp))
            .border(1.dp, RebornTopBarDividerGray, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(fontSize = 14.sp, color = Color.Black),
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { innerField ->
                if (value.isEmpty()) {
                    Text(text = "이름 또는 소속을 입력해주세요", fontSize = 14.sp, color = RebornSlateGray)
                }
                innerField()
            }
        )
    }
}
