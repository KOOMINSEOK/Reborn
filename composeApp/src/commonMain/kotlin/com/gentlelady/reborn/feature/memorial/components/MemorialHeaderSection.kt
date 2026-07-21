// composeApp/src/commonMain/kotlin/com/gentlelady/reborn/feature/memorial/components/MemorialHeaderSection.kt
package com.gentlelady.reborn.feature.memorial.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.core.theme.*
import com.gentlelady.reborn.ic_clover
import com.gentlelady.reborn.memorial.presentation.MemorialOwnerType
import com.gentlelady.reborn.memorial.presentation.MemorialProfileData
import org.jetbrains.compose.resources.painterResource
import androidx.compose.runtime.getValue
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun MemorialHeaderSection(
    profile: MemorialProfileData,
    ownerType: MemorialOwnerType,
    onEditProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(top = 16.dp, bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. 프로필 아바타
        Surface(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            color = RebornDividerGray
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = profile.name.take(1),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = RebornSlateGray
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2. 이름 및 클로버 수 배지
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = profile.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = RebornLightBlueBg
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_clover),
                        contentDescription = "Clover",
                        tint = RebornCobaltBlue,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${profile.followerCount}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = RebornCobaltBlue
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 3. 핸들(@username)
        Text(
            text = "@${profile.handle}",
            fontSize = 13.sp,
            color = RebornSlateGray
        )

        // 4. 한줄 소개 (bio)
        if (profile.bio.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "\"${profile.bio}\"",
                fontSize = 13.sp,
                color = RebornSlateGray
            )
        }

        // 5. 본인 관점 페이지(MY_MEMORIAL)용 [프로필 편집] 파란색 아웃라인 버튼 (시안 2 매칭)
        if (ownerType == MemorialOwnerType.MY_MEMORIAL) {
            Spacer(modifier = Modifier.height(12.dp))

            // 1. 터치 상태 감지용 InteractionSource
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()

            // 2. 터치 여부에 따른 동적 색상 매핑
            val buttonBorderColor = if (isPressed) RebornCobaltBlue else RebornDividerGray
            val buttonTextColor = if (isPressed) RebornCobaltBlue else Color.Black

            OutlinedButton(
                onClick = onEditProfileClick,
                interactionSource = interactionSource, // interactionSource 연결
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, buttonBorderColor), // 눌렸을 때 파란 테두리
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 6.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text(
                    text = "프로필 편집",
                    fontSize = 13.sp,
                    color = buttonTextColor, // 눌렸을 때 파란 글자
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
// Direct Injection 프리뷰
@Preview
@Composable
private fun MemorialHeaderSectionOtherPreview() {
    MaterialTheme {
        Surface {
            MemorialHeaderSection(
                profile = MemorialProfileData(
                    name = "홍길동",
                    handle = "uexjurjece",
                    bio = "인생, 헤맨만큼 내 땅이다",
                    followerCount = 5
                ),
                ownerType = MemorialOwnerType.OTHER_MEMORIAL,
                onEditProfileClick = {}
            )
        }
    }
}

@Preview
@Composable
private fun MemorialHeaderSectionMyPreview() {
    MaterialTheme {
        Surface {
            MemorialHeaderSection(
                profile = MemorialProfileData(
                    name = "이윤주",
                    handle = "uexjurjece",
                    bio = "Forever in our hearts, guiding us with love and light.",
                    followerCount = 12
                ),
                ownerType = MemorialOwnerType.MY_MEMORIAL,
                onEditProfileClick = {}
            )
        }
    }
}