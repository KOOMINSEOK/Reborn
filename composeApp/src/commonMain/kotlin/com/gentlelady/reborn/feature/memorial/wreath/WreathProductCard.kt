// composeApp/src/commonMain/kotlin/com/gentlelady/reborn/feature/memorial/wreath/WreathProductCard.kt
package com.gentlelady.reborn.feature.memorial.wreath

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.core.designsystem.components.RebornCard
import com.gentlelady.reborn.core.theme.RebornCheckGreen
import com.gentlelady.reborn.core.theme.RebornCobaltBlue
import com.gentlelady.reborn.core.theme.RebornLightBlueBg
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.ic_flower_plant
import com.gentlelady.reborn.img_wreath_basic
import com.gentlelady.reborn.img_wreath_premium
import com.gentlelady.reborn.img_wreath_special
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * 화환 상품(등급) 하나를 소개하는 카드. 여러 등급(기본형/프리미엄형/스페셜형)이 생겨도
 * 데이터만 바꿔서 재사용할 수 있도록 텍스트/이미지/버튼/강조색 전부 파라미터로 받는다.
 *
 * [imageRes]로 img_wreath_basic / img_wreath_premium / img_wreath_special 같은 화환 이미지를 넘기면
 * 그대로 표시되고, 비워두면 플레이스홀더가 표시된다.
 * [accentColor]는 가격 텍스트와 버튼 색상에 쓰인다 (기본형은 무채색, 프리미엄/스페셜은 파란색 강조 등급 차등).
 */
@Composable
fun WreathProductCard(
    title: String,
    price: String,
    description: String,
    features: List<String>,
    buttonText: String,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageRes: DrawableResource? = null,
    accentColor: Color = RebornCobaltBlue
) {
    // 카드 자체가 길쭉한 비율을 갖도록 최대 폭을 스스로 제한한다 (호출부의 fillMaxWidth와 무관하게 항상 적용)
    RebornCard(modifier = modifier.widthIn(max = 300.dp)) {
        // 1. 상단: 제목/가격 + 화환 이미지
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = price,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                }
            }

            WreathImageThumbnail(imageRes = imageRes)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2. 설명
        Text(
            text = description,
            fontSize = 13.sp,
            color = RebornSlateGray,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 3. 체크리스트
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            features.forEach { feature ->
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = RebornCheckGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = feature,
                        fontSize = 13.sp,
                        color = Color.Black,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 4. 구매 버튼
        Button(
            onClick = onButtonClick,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = accentColor),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                text = buttonText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun WreathImageThumbnail(
    imageRes: DrawableResource?,
    modifier: Modifier = Modifier
) {
    if (imageRes != null) {
        // 실제 화환 이미지 (배경은 원본 에셋 그대로 사용)
        Image(
            painter = painterResource(imageRes),
            contentDescription = null,
            modifier = modifier.size(width = 62.dp, height = 78.dp),
            contentScale = ContentScale.Fit
        )
    } else {
        // 실제 화환 이미지가 없을 때 보여주는 플레이스홀더
        Box(
            modifier = modifier
                .size(width = 62.dp, height = 78.dp)
                .background(color = RebornLightBlueBg, shape = RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color = Color.White, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_flower_plant),
                    contentDescription = null,
                    tint = RebornCobaltBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun WreathProductCardBasicPreview() {
    MaterialTheme {
        Surface {
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
                onButtonClick = {},
                imageRes = Res.drawable.img_wreath_basic,
                accentColor = RebornSlateGray,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview
@Composable
private fun WreathProductCardPremiumPreview() {
    MaterialTheme {
        Surface {
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
                onButtonClick = {},
                imageRes = Res.drawable.img_wreath_premium,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview
@Composable
private fun WreathProductCardSpecialPreview() {
    MaterialTheme {
        Surface {
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
                onButtonClick = {},
                imageRes = Res.drawable.img_wreath_special,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
