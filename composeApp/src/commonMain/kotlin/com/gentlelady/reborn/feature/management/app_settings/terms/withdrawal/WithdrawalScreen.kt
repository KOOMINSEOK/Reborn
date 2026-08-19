package com.gentlelady.reborn.feature.management.app_settings.terms.withdrawal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.core.designsystem.components.RebornBackTopAppBar
import com.gentlelady.reborn.core.theme.RebornDangerRed
import com.gentlelady.reborn.core.theme.RebornGridBorderGray
import com.gentlelady.reborn.core.theme.RebornImageBg
import com.gentlelady.reborn.core.theme.RebornSlateGray
import org.jetbrains.compose.ui.tooling.preview.Preview

private val cautionBullets = listOf(
    "회원님의 모든 개인정보 및 설정 내역이 영구적으로 삭제됩니다.",
    "생전에 예약해 두신 사후 피드 및 리마인드 메시지가 모두 취소되며 파기됩니다.",
    "다른 사용자의 메모리얼에 남기신 방명록과 화환 내역은 즉시 삭제되며 복구할 수 없습니다."
)

@Composable
fun WithdrawalScreen(
    onBackClick: () -> Unit,
    onClickWithdraw: () -> Unit
) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            RebornBackTopAppBar(title = "회원 탈퇴", onBackClick = onBackClick)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
        ) {
            Text(
                text = "RE:BORN 앱을\n탈퇴하시겠습니까?",
                color = Color.Black,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 30.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "탈퇴 전 아래 유의사항을 반드시 확인해 주세요.",
                color = RebornSlateGray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = RebornImageBg, shape = RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                cautionBullets.forEach { bullet ->
                    Row(modifier = Modifier.padding(bottom = 12.dp)) {
                        Text(text = "•  ", color = RebornSlateGray, fontSize = 14.sp)
                        Text(text = bullet, color = RebornSlateGray, fontSize = 14.sp, lineHeight = 20.sp)
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(bottom = 12.dp),
                    thickness = 1.dp,
                    color = RebornGridBorderGray
                )

                Text(
                    text = "탈퇴 후에는 어떠한 경우에도 데이터를 복구할 수 없습니다.",
                    color = RebornDangerRed,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onClickWithdraw,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RebornDangerRed)
            ) {
                Text(text = "회원 탈퇴 및 데이터 영구 삭제하기", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview
@Composable
private fun WithdrawalScreenPreview() {
    MaterialTheme {
        WithdrawalScreen(onBackClick = {}, onClickWithdraw = {})
    }
}
