package com.gentlelady.reborn.feature.management.app_settings.terms.privacy_policy

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

private data class PolicySection(val title: String, val body: String)

private val sections = listOf(
    PolicySection(
        "1. 수집하는 개인정보 항목",
        "회사는 회원가입, 고객상담, 메모리얼 및 리마인드 서비스 제공을 위해 아래와 같은 개인정보를 수집합니다.\n- 필수항목: 이메일, 비밀번호, 이름, 생년월일, 사후 연락처"
    ),
    PolicySection(
        "2. 개인정보의 이용 목적",
        "수집된 개인정보는 생전 서비스 이용 및 결제 관리뿐 아니라, 사후 메모리얼 페이지 전환, 예약된 리마인드 메시지의 정확한 발송 등 동일한 사후 서비스 제공을 위해 활용됩니다."
    ),
    PolicySection(
        "3. 사후 데이터 보호 정책",
        "회원의 사망이 인증된 경우, 생전에 설정한 '계정 공개 범위'에 따라 지정된 팔로워 및 방문자에게 한정적으로 데이터가 공개되며, 그 외 개인정보는 철저히 암호화되어 보호됩니다."
    ),
    PolicySection(
        "4. 개인정보의 파기",
        "회원 탈퇴 요청 시, 법령에 따라 별도 보관이 필요한 정보를 제외한 모든 데이터(방명록, 예약 피드 등)는 지체 없이 영구 파기됩니다."
    )
)

@Composable
fun PrivacyPolicyScreen(onBackClick: () -> Unit) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            RebornBackTopAppBar(title = "개인정보처리방침", onBackClick = onBackClick)
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
private fun PrivacyPolicyScreenPreview() {
    MaterialTheme {
        PrivacyPolicyScreen(onBackClick = {})
    }
}
