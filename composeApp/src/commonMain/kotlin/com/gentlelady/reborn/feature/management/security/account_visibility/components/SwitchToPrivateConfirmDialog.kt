package com.gentlelady.reborn.feature.management.security.account_visibility.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Visibility
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.gentlelady.reborn.core.theme.RebornCobaltBlue
import com.gentlelady.reborn.core.theme.RebornSlateGray
import org.jetbrains.compose.ui.tooling.preview.Preview

private data class ConfirmBullet(val icon: ImageVector, val text: String)

private val bullets = listOf(
    ConfirmBullet(
        Icons.Default.Visibility,
        "팔로워만 회원님의 사진과 동영상을 볼 수 있습니다."
    ),
    ConfirmBullet(
        Icons.Default.AlternateEmail,
        "회원님이 메시지를 보내거나 회원님을 태그하는 경우, 팔로우하지 않는 사람에게는 회원님을 알아보기 어려운 정보만 표시됩니다."
    ),
    ConfirmBullet(
        Icons.Default.History,
        "이미 공유 중인 콘텐츠 및 게시물, 태그 및 재게시 콘텐츠는 삭제되지 않습니다. 24시간 이내에 공개 계정으로 다시 전환하는 경우 복원됩니다."
    )
)

/**
 * "공개 계정" → "비공개 계정" 전환 직전 노출하는 확인 다이얼로그.
 */
@Composable
internal fun SwitchToPrivateConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "비공개 계정으로 전환하시겠어요?",
                    color = Color.Black,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                bullets.forEach { bullet ->
                    Row(modifier = Modifier.padding(bottom = 14.dp)) {
                        Box(
                            modifier = Modifier.size(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = bullet.icon,
                                contentDescription = null,
                                tint = RebornSlateGray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = bullet.text,
                            color = RebornSlateGray,
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

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
            }
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
