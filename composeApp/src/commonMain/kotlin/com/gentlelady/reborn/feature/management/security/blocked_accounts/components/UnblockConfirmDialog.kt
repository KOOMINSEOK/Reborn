package com.gentlelady.reborn.feature.management.security.blocked_accounts.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.core.designsystem.components.CircleAvatarImage
import com.gentlelady.reborn.core.theme.RebornCobaltBlue
import com.gentlelady.reborn.core.theme.RebornGridBorderGray
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.img_memorial_profile_dummy
import com.gentlelady.reborn.management.security.blocked_accounts.domain.model.BlockedAccountItem
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * 차단 해제 직전 노출하는 확인 다이얼로그.
 */
@Composable
internal fun UnblockConfirmDialog(
    target: BlockedAccountItem,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(20.dp), color = Color.White) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircleAvatarImage(
                    imageRes = target.avatar,
                    size = 64.dp,
                    fallbackText = target.name,
                    borderWidth = 0.dp,
                    shadowElevation = 0.dp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "${target.name}님 계정 차단을\n해제하시겠습니까?",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "이제 이 계정에서 회원님의 프로필과 \n" +
                            "게시물을 다시 볼 수 있으며, 서로 메시지를 \n" +
                            "보낼 수 있습니다.",
                    color = RebornSlateGray,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, RebornGridBorderGray)
                    ) {
                        Text(text = "취소", color = RebornSlateGray, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RebornCobaltBlue)
                    ) {
                        Text(text = "차단 해제", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun UnblockConfirmDialogPreview() {
    MaterialTheme {
        UnblockConfirmDialog(
            target = BlockedAccountItem("1", "김민수", "min_su_99", Res.drawable.img_memorial_profile_dummy),
            onConfirm = {},
            onDismiss = {}
        )
    }
}
