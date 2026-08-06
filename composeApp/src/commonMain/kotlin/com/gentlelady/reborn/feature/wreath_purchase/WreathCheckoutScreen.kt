// composeApp/src/commonMain/kotlin/com/gentlelady/reborn/feature/wreath_purchase/WreathCheckoutScreen.kt
package com.gentlelady.reborn.feature.wreath_purchase

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.core.designsystem.components.RebornBackTopAppBar
import com.gentlelady.reborn.core.designsystem.components.RebornCard
import com.gentlelady.reborn.core.theme.RebornBackgroundGray
import com.gentlelady.reborn.core.theme.RebornCobaltBlue
import com.gentlelady.reborn.core.theme.RebornGridBorderGray
import com.gentlelady.reborn.core.theme.RebornGridIconGray
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.wreathpurchase.presentation.WreathIntent
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

private data class PaymentMethod(val id: String, val label: String)

private val paymentMethods = listOf(
    PaymentMethod("card", "신용/체크카드"),
    PaymentMethod("kakao", "카카오페이"),
    PaymentMethod("naver", "네이버페이"),
    PaymentMethod("toss", "토스페이")
)

/** 화환 결제 화면. 상단에 선택한 등급의 주문 상품(이미지/이름/금액)을 보여주고 결제 수단을 고를 수 있다. */
@Composable
fun WreathCheckoutScreen(
    tier: String?,
    selectedPaymentMethod: String,
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
                title = "결제하기",
                onBackClick = { onIntent(WreathIntent.ClickBack) }
            )
        },
        bottomBar = {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "총 결제 금액", fontSize = 14.sp, color = Color.Black)
                    Text(
                        text = tierInfo.price,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = RebornCobaltBlue
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {},
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RebornCobaltBlue),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Text(
                        text = "${tierInfo.price.removePrefix("₩")}원 결제하기",
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
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(text = "주문 상품", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(16.dp))

            RebornCard(
                backgroundColor = RebornBackgroundGray,
                borderWidth = 0.dp,
                contentPadding = 20.dp
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(tierInfo.imageRes),
                        contentDescription = null,
                        modifier = Modifier.size(width = 64.dp, height = 80.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = tierInfo.displayName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = tierInfo.price,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = RebornCobaltBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            InfoLine("결제 완료 후 화환 디자인 선택 및 추모 메시지를 작성할 수 있습니다.")
            Spacer(modifier = Modifier.height(6.dp))
            InfoLine("온라인 화환은 결제 완료 후 취소 및 환불이 불가합니다.")

            Spacer(modifier = Modifier.height(28.dp))
            Text(text = "결제 수단", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                paymentMethods.forEach { method ->
                    PaymentMethodRow(
                        label = method.label,
                        isSelected = method.id == selectedPaymentMethod,
                        onClick = { onIntent(WreathIntent.SelectPaymentMethod(method.id)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoLine(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
            tint = RebornCobaltBlue,
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            fontSize = 11.sp,
            color = RebornCobaltBlue,
            maxLines = 1
        )
    }
}

@Composable
private fun PaymentMethodRow(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    RebornCard(
        contentPadding = 14.dp,
        borderColor = if (isSelected) RebornCobaltBlue else RebornGridBorderGray,
        borderWidth = if (isSelected) 2.dp else 1.dp,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            PaymentMethodIcon(label = label)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                fontSize = 15.sp,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = RebornCobaltBlue,
                    modifier = Modifier.size(22.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = RebornSlateGray,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun PaymentMethodIcon(label: String) {
    val (bg, textColor, char) = when (label) {
        "카카오페이" -> Triple(Color(0xFFFEE500), Color.Black, "K")
        "네이버페이" -> Triple(Color(0xFF03C75A), Color.White, "N")
        "토스페이" -> Triple(Color(0xFF1B64DA), Color.White, "T")
        else -> Triple(RebornGridIconGray, RebornCobaltBlue, null)
    }
    Surface(
        shape = CircleShape,
        color = bg,
        modifier = Modifier.size(28.dp)
    ) {
        if (char != null) {
            Text(
                text = char,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxSize().wrapContentHeight(Alignment.CenterVertically)
            )
        } else {
            Box(modifier = Modifier.fillMaxSize().background(bg, CircleShape), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.CreditCard,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun WreathCheckoutScreenPreview() {
    MaterialTheme {
        WreathCheckoutScreen(
            tier = "special",
            selectedPaymentMethod = "card",
            onIntent = {}
        )
    }
}
