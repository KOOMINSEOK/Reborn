package com.gentlelady.reborn.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.ic_home_memorial
import com.gentlelady.reborn.ic_home_notifications
import com.gentlelady.reborn.core.theme.RebornCobaltBlue
import com.gentlelady.reborn.core.theme.RebornLightBlueBg
import com.gentlelady.reborn.core.theme.RebornBorderLightBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar() {
    TopAppBar(
        title = { Text("Home", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold) },
        actions = {
            IconButton(
                onClick = { },
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(RebornLightBlueBg)
                    .border(width = 1.dp, color = RebornBorderLightBlue, shape = CircleShape)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_home_memorial),
                    contentDescription = "Memorial Page",
                    tint = RebornCobaltBlue,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                IconButton(onClick = { }) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_home_notifications),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Surface(
                    modifier = Modifier
                        .size(6.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = (-2).dp, y = 2.dp),
                    color = Color.Red,
                    shape = CircleShape
                ) {}
            }
            Spacer(modifier = Modifier.width(16.dp))
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
    )
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