// composeApp/src/commonMain/kotlin/com/gentlelady/reborn/feature/memorial/components/MemorialHeaderSection.kt
package com.gentlelady.reborn.feature.memorial.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.core.designsystem.components.CircleAvatarImage
import com.gentlelady.reborn.core.designsystem.components.CircleIconBadge
import com.gentlelady.reborn.core.theme.*
import com.gentlelady.reborn.ic_flower_plant
import com.gentlelady.reborn.memorial.presentation.MemorialProfileData
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun MemorialHeaderSection(
    profile: MemorialProfileData,
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
        // 1. 프로필 아바타 (공용 CircleAvatarImage 컴포넌트 사용)
        val avatarSize = 80.dp
        Box {
            CircleAvatarImage(
                imageRes = profile.profileImageRes,
                size = avatarSize,
                fallbackText = profile.name,
                borderWidth = 0.dp
            )

            // 메모리얼 공간임을 나타내는 원형 배지 (아바타 우하단 오버레이, 아바타 대비 비율 고정)
            CircleIconBadge(
                icon = Res.drawable.ic_flower_plant,
                contentDescription = "메모리얼 마크",
                size = avatarSize * 0.55f,
                backgroundColor = RebornCobaltBlue,
                iconTint = Color.White,
                borderWidth = 3.dp,
                borderColor = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 16.dp, y = 4.dp)
            )
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
            Icon(
                painter = painterResource(Res.drawable.ic_flower_plant),
                contentDescription = "메모리얼 마크",
                tint = RebornCobaltBlue,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = "${profile.followerCount}",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = RebornCobaltBlue
            )
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
                onEditProfileClick = {}
            )
        }
    }
}