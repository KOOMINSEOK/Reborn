package com.gentlelady.reborn.feature.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.ic_nav_home_default
import com.gentlelady.reborn.core.theme.RebornDeepBlue
import com.gentlelady.reborn.core.theme.RebornUnselectedGray

private val CommonIconSize = 28.dp

@Composable
fun RowScope.RebornNavItem(
    iconRes: org.jetbrains.compose.resources.DrawableResource,
    label: String,
    isSelected: Boolean,
    colors: NavigationBarItemColors,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = isSelected,
        onClick = onClick,
        icon = {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = label,
                modifier = Modifier.size(CommonIconSize)
            )
        },
        label = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(label)
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .background(
                            color = if (isSelected) RebornDeepBlue else Color.Transparent,
                            shape = CircleShape
                        )
                )
            }
        },
        colors = colors
    )
}

@Preview
@Composable
fun RebornNavItemPreview() {
    MaterialTheme {
        Surface {
            NavigationBar {
                val navItemColors = NavigationBarItemDefaults.colors(
                    selectedIconColor = RebornDeepBlue,
                    selectedTextColor = RebornDeepBlue,
                    unselectedIconColor = RebornUnselectedGray,
                    unselectedTextColor = RebornUnselectedGray,
                    indicatorColor = Color.Transparent
                )
                RebornNavItem(
                    iconRes = Res.drawable.ic_nav_home_default,
                    label = "Home",
                    isSelected = true,
                    colors = navItemColors,
                    onClick = {}
                )
            }
        }
    }
}