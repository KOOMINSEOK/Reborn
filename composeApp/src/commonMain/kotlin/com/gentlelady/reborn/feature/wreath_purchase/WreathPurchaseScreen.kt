// composeApp/src/commonMain/kotlin/com/gentlelady/reborn/feature/wreathpurchase/WreathPurchaseScreen.kt
package com.gentlelady.reborn.feature.wreath_purchase

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.core.designsystem.components.RebornBackTopAppBar
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.img_wreath_basic
import com.gentlelady.reborn.img_wreath_premium
import com.gentlelady.reborn.img_wreath_special
import com.gentlelady.reborn.wreathpurchase.presentation.WreathIntent
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * 온라인 화환 구매 화면. 스페셜형 → 기본형 → 프리미엄형 순으로 등급 카드를 세로로 나열한다.
 * 각 카드는 [WreathProductCard]를 그대로 재사용하고, 이 화면은 배치/여백만 담당한다.
 */
@Composable
fun WreathPurchaseScreen(
    selectedTier: String?,
    onIntent: (WreathIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.White,
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            RebornBackTopAppBar(
                title = "온라인 화환 구매",
                onBackClick = { onIntent(WreathIntent.ClickBack) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            WreathProductCard(
                title = "스페셜형",
                price = "₩4,900",
                description = "조금 더 특별하게 마음을 표현해보세요.",
                features = listOf(
                    "화환 그래픽 업그레이드",
                    "꽃 디자인 선택 가능 (3종 중 택 1)",
                    "추모 텍스트 작성 (글자 수 200자 제한)"
                ),
                buttonText = "스페셜 화환 보내기",
                onButtonClick = { onIntent(WreathIntent.ClickBuy("special")) },
                imageRes = Res.drawable.img_wreath_special,
                isSelected = selectedTier == "special",
                onCardClick = { onIntent(WreathIntent.SelectTier("special")) },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )

            WreathProductCard(
                title = "기본형",
                price = "₩900",
                description = "정갈하고 차분한 마음을 전하는 기본 추모 화환입니다.",
                features = listOf(
                    "스탠다드 디자인의 근조화환 그래픽 제공",
                    "기본 흑/백 리본 색상 고정",
                    "추모 텍스트 작성 (글자 수 100자 제한)"
                ),
                buttonText = "기본 화환 보내기",
                onButtonClick = { onIntent(WreathIntent.ClickBuy("basic")) },
                imageRes = Res.drawable.img_wreath_basic,
                accentColor = RebornSlateGray,
                isSelected = selectedTier == "basic",
                onCardClick = { onIntent(WreathIntent.SelectTier("basic")) },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )

            WreathProductCard(
                title = "프리미엄형",
                price = "₩9,900",
                description = "가장 풍성한 화환으로 고인에게 깊은 애도를 표합니다.",
                features = listOf(
                    "최고급 디테일 프리미엄 화환 그래픽",
                    "꽃 디자인 선택 가능",
                    "리본 색상 커스텀 가능",
                    "추모 메시지 글자 수 무제한",
                    "사진 1장 첨부 가능",
                    "화환 테두리 후광 효과"
                ),
                buttonText = "프리미엄 화환 보내기",
                onButtonClick = { onIntent(WreathIntent.ClickBuy("premium")) },
                imageRes = Res.drawable.img_wreath_premium,
                isSelected = selectedTier == "premium",
                onCardClick = { onIntent(WreathIntent.SelectTier("premium")) },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Preview
@Composable
private fun WreathPurchaseScreenPreview() {
    MaterialTheme {
        Surface {
            WreathPurchaseScreen(
                selectedTier = "special",
                onIntent = {}
            )
        }
    }
}
