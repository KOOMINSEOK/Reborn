// composeApp/src/commonMain/kotlin/com/gentlelady/reborn/feature/wreath_purchase/WreathSpecialMessageScreen.kt
package com.gentlelady.reborn.feature.wreath_purchase

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.core.designsystem.components.RebornBackTopAppBar
import com.gentlelady.reborn.core.theme.RebornCobaltBlue
import com.gentlelady.reborn.core.theme.RebornGridBorderGray
import com.gentlelady.reborn.core.theme.RebornImageBg
import com.gentlelady.reborn.core.theme.RebornLightBlueBg
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.core.theme.RebornTopBarDividerGray
import com.gentlelady.reborn.feature.wreath_purchase.components.WreathFlowerDesign
import com.gentlelady.reborn.feature.wreath_purchase.components.WreathMessageTextArea
import com.gentlelady.reborn.feature.wreath_purchase.components.WreathSenderNameField
import com.gentlelady.reborn.feature.wreath_purchase.components.wreathFlowerDesignLabel
import com.gentlelady.reborn.feature.wreath_purchase.components.wreathFlowerDesigns
import com.gentlelady.reborn.feature.wreath_purchase.components.wreathTierInfo
import com.gentlelady.reborn.ic_flower_plant
import com.gentlelady.reborn.wreathpurchase.presentation.WreathIntent
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

private const val MESSAGE_MAX_LENGTH = 200

/** 스페셜형 화환 전용 꾸미기 화면. 꽃 디자인 선택 + 추모 텍스트 + 보내는 이를 입력받는다. */
@Composable
fun WreathSpecialMessageScreen(
    tier: String?,
    selectedFlowerDesign: String,
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
                title = "스페셜 화환 꾸미기",
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
                        text = "스페셜 화환 남기기",
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
                    .height(220.dp)
                    .background(RebornImageBg, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(12.dp))
            SelectedFlowerChip(
                label = wreathFlowerDesignLabel(selectedFlowerDesign),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(28.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "1. 꽃 디자인 선택", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "원하시는 디자인을 선택해주세요", fontSize = 12.sp, color = RebornSlateGray)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                wreathFlowerDesigns.forEach { design ->
                    FlowerDesignCard(
                        design = design,
                        isSelected = design.id == selectedFlowerDesign,
                        onClick = { onIntent(WreathIntent.SelectFlowerDesign(design.id)) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "2. 추모 텍스트 (최대 ${MESSAGE_MAX_LENGTH}자)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(10.dp))
            WreathMessageTextArea(
                value = messageText,
                onValueChange = { onIntent(WreathIntent.ChangeMessageText(it)) },
                maxLength = MESSAGE_MAX_LENGTH,
                placeholder = "예) 따뜻했던 미소를 영원히 기억하겠습니다. 그곳에서는 아프지 말고 평안하시길 바랍니다."
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "3. 보내는 이", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(10.dp))
            WreathSenderNameField(
                value = senderName,
                onValueChange = { onIntent(WreathIntent.ChangeSenderName(it)) }
            )
        }
    }
}

@Composable
private fun SelectedFlowerChip(label: String, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(RebornLightBlueBg)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_flower_plant),
            contentDescription = null,
            tint = RebornCobaltBlue,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = "$label (선택됨)", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = RebornCobaltBlue)
    }
}

@Composable
private fun FlowerDesignCard(
    design: WreathFlowerDesign,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(if (isSelected) RebornLightBlueBg else RebornImageBg)
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) RebornCobaltBlue else RebornGridBorderGray,
                    shape = RoundedCornerShape(14.dp)
                )
                .clickable(onClick = onClick)
                .padding(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_flower_plant),
                    contentDescription = null,
                    tint = design.accentColor,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = design.label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }

        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-8).dp, y = 8.dp)
                    .size(22.dp)
                    .border(2.dp, Color.White, CircleShape)
                    .background(RebornCobaltBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(13.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun WreathSpecialMessageScreenPreview() {
    MaterialTheme {
        Surface {
            WreathSpecialMessageScreen(
                tier = "special",
                selectedFlowerDesign = "lily",
                messageText = "",
                senderName = "",
                onIntent = {}
            )
        }
    }
}
