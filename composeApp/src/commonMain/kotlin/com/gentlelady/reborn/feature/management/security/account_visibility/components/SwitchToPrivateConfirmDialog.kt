package com.gentlelady.reborn.feature.management.security.account_visibility.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.core.theme.RebornCobaltBlue
import com.gentlelady.reborn.core.theme.RebornDeepBlue
import com.gentlelady.reborn.core.theme.RebornIconBoxBlue
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.ic_at_sign
import com.gentlelady.reborn.ic_home_memorial
import com.gentlelady.reborn.ic_play
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

private data class ConfirmBullet(val icon: DrawableResource, val text: String)

private val bullets = listOf(
    ConfirmBullet(
        icon = Res.drawable.ic_play,
        text = "회원님의 팔로워만 회원님의 사진과 동영상을 볼 수 있습니다."
    ),
    ConfirmBullet(
        icon = Res.drawable.ic_at_sign,
        text = "회원님에게 메시지를 보내거나 회원님을 태그 또는 @언급할 수 있는 사람은 변경되지 않지만, 회원님을 팔로우하지 않는 사람을 태그할 수 없게 됩니다."
    ),
    ConfirmBullet(
        icon = Res.drawable.ic_home_memorial,
        text = "아무도 회원님의 콘텐츠를 다시 사용할 수 없습니다. 이전에 다른 사용자가 리믹스하거나 사용한 회원님의 게시물 및 메모리얼 콘텐츠가 모두 삭제됩니다. 24시간 이내에 공개 계정으로 다시 전환하는 경우 복원됩니다."
    )
)

/**
 * "공개 계정" → "비공개 계정" 전환 직전 화면 하단에서 올라오는 확인 드로어.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SwitchToPrivateConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
            Text(
                text = "비공개 계정으로 전환하시겠어요?",
                color = Color.Black,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            bullets.forEach { bullet ->
                Row(modifier = Modifier.padding(bottom = 20.dp)) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = RebornIconBoxBlue,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(bullet.icon),
                                contentDescription = null,
                                tint = RebornDeepBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = bullet.text,
                        color = RebornSlateGray,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RebornCobaltBlue)
            ) {
                Text(text = "비공개로 전환", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Preview
@Composable
private fun SwitchToPrivateConfirmDialogPreview() {
    MaterialTheme {
        SwitchToPrivateConfirmDialog(onConfirm = {}, onDismiss = {})
    }
}
