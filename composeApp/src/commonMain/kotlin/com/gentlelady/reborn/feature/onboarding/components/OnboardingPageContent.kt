package com.gentlelady.reborn.feature.onboarding.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.core.designsystem.components.RebornCard
import com.gentlelady.reborn.core.theme.RebornTextPrimary
import com.gentlelady.reborn.core.theme.RebornTextSecondary
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun OnboardingPageContent(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    mockup: @Composable () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RebornCard {
            mockup()
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                color = RebornTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = description,
                color = RebornTextSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
private fun OnboardingPageContentPreview() {
    MaterialTheme {
        OnboardingPageContent(
            title = "함께했던 추억을 모아 완성하는\n단 하나의 공간.",
            description = "갤러리에 잠든 사진과 지인들의 방명록\n을 모아 고인의 메모리얼 페이지를 완성\n하세요."
        ) {
            Text(text = "목업 콘텐츠 영역", modifier = Modifier.padding(24.dp))
        }
    }
}
