package com.gentlelady.reborn.feature.onboarding.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.core.designsystem.components.RebornCard
import com.gentlelady.reborn.img_onboarding_1
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun OnboardingFlowerMockup() {
    RebornCard {
        Image(
            painter = painterResource(Res.drawable.img_onboarding_1),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxWidth().height(220.dp)
        )
    }
}

@Preview
@Composable
private fun OnboardingFlowerMockupPreview() {
    MaterialTheme {
        OnboardingFlowerMockup()
    }
}
