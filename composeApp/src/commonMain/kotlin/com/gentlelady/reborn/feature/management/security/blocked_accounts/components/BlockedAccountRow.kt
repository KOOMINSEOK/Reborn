package com.gentlelady.reborn.feature.management.security.blocked_accounts.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.core.designsystem.components.CircleAvatarImage
import com.gentlelady.reborn.core.theme.RebornGridBorderGray
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.img_memorial_profile_dummy
import com.gentlelady.reborn.management.security.blocked_accounts.domain.model.BlockedAccountItem
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun BlockedAccountRow(
    item: BlockedAccountItem,
    onClickUnblock: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleAvatarImage(
            imageRes = item.avatar,
            size = 48.dp,
            fallbackText = item.name,
            borderWidth = 0.dp,
            shadowElevation = 0.dp
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.name, color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = "@${item.username}", color = RebornSlateGray, fontSize = 13.sp)
        }

        Button(
            onClick = onClickUnblock,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            border = BorderStroke(1.dp, RebornGridBorderGray),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Text(text = "차단 해제", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Preview
@Composable
private fun BlockedAccountRowPreview() {
    MaterialTheme {
        BlockedAccountRow(
            item = BlockedAccountItem("1", "김민수", "min_su_99", Res.drawable.img_memorial_profile_dummy),
            onClickUnblock = {}
        )
    }
}
