package com.gentlelady.reborn.feature.management.app_settings.customer_support.inquiry

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.core.designsystem.components.RebornBackTopAppBar
import com.gentlelady.reborn.core.theme.RebornBackgroundGray
import com.gentlelady.reborn.core.theme.RebornKakaoYellow
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.ic_comment
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun InquiryScreen(
    onBackClick: () -> Unit,
    onClickKakaoChannel: () -> Unit
) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            RebornBackTopAppBar(title = "1:1 문의", onBackClick = onBackClick)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(color = RebornBackgroundGray, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_comment),
                    contentDescription = null,
                    tint = RebornSlateGray,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "빠르고 정확한 상담을 위해\n카카오톡 1:1 문의를 이용해 주세요.",
                color = Color.Black,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "운영 시간 : 매일 10:00 ~ 18:00",
                color = RebornSlateGray,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onClickKakaoChannel,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RebornKakaoYellow)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_comment),
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "카카오톡 채널로 이동하기", color = Color.Black, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Preview
@Composable
private fun InquiryScreenPreview() {
    MaterialTheme {
        InquiryScreen(onBackClick = {}, onClickKakaoChannel = {})
    }
}
