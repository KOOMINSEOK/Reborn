package com.gentlelady.reborn.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.ic_home_notifications
import com.gentlelady.reborn.core.theme.RebornCobaltBlue
import com.gentlelady.reborn.core.theme.RebornGridIconGray
import com.gentlelady.reborn.core.theme.RebornTopBarDividerGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    onAddClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    hasUnreadNotification: Boolean = false
) {
    Column {
        TopAppBar(
            title = { Text("홈", fontSize = 22.sp, fontWeight = FontWeight.Medium) },
            actions = {
                FilledIconButton(
                    onClick = onAddClick,
                    modifier = Modifier.size(36.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = RebornGridIconGray,
                        contentColor = Color.Black
                    )
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "게시글 작성", modifier = Modifier.size(22.dp))
                }

                Spacer(modifier = Modifier.width(10.dp))

                Box(modifier = Modifier.size(36.dp)) {
                    FilledIconButton(
                        onClick = onNotificationClick,
                        modifier = Modifier.fillMaxSize(),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = RebornGridIconGray,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_home_notifications),
                            contentDescription = "알림",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    if (hasUnreadNotification) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = (-3).dp, y = 3.dp)
                                .size(8.dp)
                                .background(RebornCobaltBlue, CircleShape)
                                .border(1.5.dp, RebornGridIconGray, CircleShape)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            windowInsets = WindowInsets(0.dp)
        )
        HorizontalDivider(thickness = 1.dp, color = RebornTopBarDividerGray)
    }
}

@Preview
@Composable
fun HomeTopAppBarPreview() {
    MaterialTheme {
        Surface {
            HomeTopAppBar()
        }
    }
}
