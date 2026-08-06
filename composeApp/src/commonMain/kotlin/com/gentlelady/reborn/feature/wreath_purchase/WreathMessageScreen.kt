// composeApp/src/commonMain/kotlin/com/gentlelady/reborn/feature/wreath_purchase/WreathMessageScreen.kt
package com.gentlelady.reborn.feature.wreath_purchase

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.core.designsystem.components.RebornBackTopAppBar
import com.gentlelady.reborn.core.theme.RebornCobaltBlue
import com.gentlelady.reborn.core.theme.RebornImageBg
import com.gentlelady.reborn.core.theme.RebornTopBarDividerGray
import com.gentlelady.reborn.feature.wreath_purchase.components.WreathMessageTextArea
import com.gentlelady.reborn.feature.wreath_purchase.components.WreathSenderNameField
import com.gentlelady.reborn.feature.wreath_purchase.components.wreathTierInfo
import com.gentlelady.reborn.wreathpurchase.presentation.WreathIntent
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

private const val MESSAGE_MAX_LENGTH = 100

/** 결제 완료 후 진입하는 추모 메시지 작성 화면(기본형). 화환 이미지, 등급 요약, 추모 텍스트/보내는 이 입력을 받는다. */
@Composable
fun WreathMessageScreen(
    tier: String?,
    messageText: String,
    senderName: String,
    onIntent: (WreathIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val tierInfo = wreathTierInfo(tier)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.White,
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            RebornBackTopAppBar(
                title = "추모 메시지 작성",
                onBackClick = { onIntent(WreathIntent.ClickBack) }
            )
        },
        bottomBar = {
            Column {
                HorizontalDivider(color = RebornTopBarDividerGray, thickness = 1.dp)
                Button(
                    onClick = { onIntent(WreathIntent.ClickSubmitMessage) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RebornCobaltBlue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .height(52.dp)
                ) {
                    Text(
                        text = "화환 남기기",
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Image(
                painter = painterResource(tierInfo.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(290.dp)
                    .background(RebornImageBg, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = tierInfo.messageSubtitle,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = RebornCobaltBlue,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "추모 텍스트 (최대 ${MESSAGE_MAX_LENGTH}자)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(10.dp))
            WreathMessageTextArea(
                value = messageText,
                onValueChange = { onIntent(WreathIntent.ChangeMessageText(it)) },
                maxLength = MESSAGE_MAX_LENGTH,
                placeholder = "예) 삼가 고인의 명복을 빕니다. 편히 쉬세요."
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "보내는 이", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(10.dp))
            WreathSenderNameField(
                value = senderName,
                onValueChange = { onIntent(WreathIntent.ChangeSenderName(it)) }
            )
        }
    }
}

@Preview
@Composable
private fun WreathMessageScreenPreview() {
    MaterialTheme {
        Surface {
            WreathMessageScreen(
                tier = "basic",
                messageText = "",
                senderName = "",
                onIntent = {}
            )
        }
    }
}
