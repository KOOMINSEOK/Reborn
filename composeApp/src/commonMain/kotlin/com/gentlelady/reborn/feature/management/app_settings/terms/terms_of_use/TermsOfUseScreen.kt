package com.gentlelady.reborn.feature.management.app_settings.terms.terms_of_use

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.core.designsystem.components.RebornBackTopAppBar
import com.gentlelady.reborn.core.theme.RebornSlateGray
import org.jetbrains.compose.ui.tooling.preview.Preview

private data class TermsSection(val title: String, val body: String)

private val sections = listOf(
    TermsSection(
        "제1조 (목적)",
        "본 약관은 RE:BORN이 제공하는 디지털 추모 및 리마인드 서비스의 이용 조건, 절차, 회원과 회사 간의 권리, 의무를 규정함을 목적으로 합니다."
    ),
    TermsSection(
        "제2조 (사후 데이터 및 계정 관리)",
        "회원이 생전에 예약한 사후 피드 및 리마인드 메시지는 회사가 정한 사망 인증 절차가 완료된 특정 시점에 공개되거나 발송 및 전달됩니다."
    ),
    TermsSection(
        "제3조 (결제 및 환불)",
        "앱 내에서 결제되는 온라인 추모 화환 등 디지털 콘텐츠 상품은 특성상 결제 즉시 완료되며, 전자상거래법에 따라 결제 완료 후 환불 청약 철회(취소 및 환불)가 불가합니다."
    ),
    TermsSection(
        "제4조 (계정 해지 및 파기)",
        "회원이 탈퇴를 요청할 경우, 관련 법령에 따라 보존해야 하는 정보를 제외한 회원의 개인정보, 방명록 기록 및 활동 내역은 즉시 영구 삭제되며 복구할 수 없습니다."
    )
)

@Composable
fun TermsOfUseScreen(onBackClick: () -> Unit) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            RebornBackTopAppBar(title = "이용약관", onBackClick = onBackClick)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                text = "시행일: 2026년 7월 29일",
                color = RebornSlateGray,
                fontSize = 13.sp,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))

            sections.forEach { section ->
                Text(text = section.title, color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = section.body, color = RebornSlateGray, fontSize = 14.sp, lineHeight = 21.sp)
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Preview
@Composable
private fun TermsOfUseScreenPreview() {
    MaterialTheme {
        TermsOfUseScreen(onBackClick = {})
    }
}
