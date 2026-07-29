package com.gentlelady.reborn.feature.onboarding.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.core.theme.RebornGridBorderGray
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.ic_clover
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun OnboardingFlowerMockup() {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent,
        border = BorderStroke(1.dp, RebornGridBorderGray),
        modifier = Modifier.fillMaxWidth().height(180.dp).padding(24.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(Res.drawable.ic_clover),
                contentDescription = null,
                tint = RebornSlateGray,
                modifier = Modifier.height(64.dp)
            )
        }
    }
}

@Preview
@Composable
private fun OnboardingFlowerMockupPreview() {
    MaterialTheme {
        OnboardingFlowerMockup()
    }
}
