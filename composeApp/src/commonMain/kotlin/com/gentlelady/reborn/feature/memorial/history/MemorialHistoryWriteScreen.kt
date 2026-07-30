// composeApp/src/commonMain/kotlin/com/gentlelady/reborn/feature/memorial/history/MemorialHistoryWriteScreen.kt
package com.gentlelady.reborn.feature.memorial.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.core.designsystem.components.CircleIconBadge
import com.gentlelady.reborn.core.theme.RebornCobaltBlue
import com.gentlelady.reborn.core.theme.RebornInputBorderGray
import com.gentlelady.reborn.core.theme.RebornLightBlueBg
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.core.theme.RebornUnselectedGray
import com.gentlelady.reborn.ic_image_add
import com.gentlelady.reborn.memorial.presentation.MemorialHistoryWriteFormState
import com.gentlelady.reborn.memorial.presentation.MemorialIntent
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemorialHistoryWriteScreen(
    formState: MemorialHistoryWriteFormState,
    onIntent: (MemorialIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { onIntent(MemorialIntent.ClickCloseHistoryWrite) }) {
                        CircleIconBadge(
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기",
                            size = 36.dp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                windowInsets = WindowInsets(0.dp)
            )
        },
        bottomBar = {
            Surface(color = Color.White) {
                Button(
                    onClick = { onIntent(MemorialIntent.ClickPostHistory) },
                    enabled = formState.imageRes != null,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RebornCobaltBlue,
                        disabledContainerColor = RebornUnselectedGray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .height(52.dp)
                ) {
                    Text(
                        text = "히스토리(추억) 게시하기",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // 1. 화면 타이틀
            Text(
                text = "히스토리 작성",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 16.dp)
            )

            // 2. 사진/영상 추가 (점선 박스)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .dashedBorder(color = RebornInputBorderGray, cornerRadius = 16.dp, strokeWidth = 2.dp)
                    .clickable { onIntent(MemorialIntent.ClickChangeHistoryImage) }
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(color = RebornLightBlueBg, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_image_add),
                        contentDescription = "사진 또는 영상 추가",
                        tint = RebornCobaltBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "사진 또는 영상 추가",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "추억이 담긴 사진이나 영상을 추가해주세요",
                    fontSize = 12.sp,
                    color = RebornSlateGray,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. 추억 이야기 입력
            Text(
                text = "추억 이야기",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.2f)
                    .border(width = 1.dp, color = RebornInputBorderGray, shape = RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                if (formState.caption.isEmpty()) {
                    Text(
                        text = "고인과 함께했던 소중한 추억의 순간을 들려주세요...",
                        fontSize = 14.sp,
                        color = RebornSlateGray,
                        lineHeight = 20.sp
                    )
                }
                BasicTextField(
                    value = formState.caption,
                    onValueChange = { onIntent(MemorialIntent.UpdateHistoryWriteCaption(it)) },
                    textStyle = TextStyle(fontSize = 14.sp, color = Color.Black, lineHeight = 20.sp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun Modifier.dashedBorder(color: Color, cornerRadius: Dp, strokeWidth: Dp = 1.dp): Modifier = drawBehind {
    drawRoundRect(
        color = color,
        cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx()),
        style = Stroke(
            width = strokeWidth.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
        )
    )
}

@Preview
@Composable
private fun MemorialHistoryWriteScreenPreview() {
    MaterialTheme {
        Surface {
            MemorialHistoryWriteScreen(
                formState = MemorialHistoryWriteFormState(),
                onIntent = {}
            )
        }
    }
}
