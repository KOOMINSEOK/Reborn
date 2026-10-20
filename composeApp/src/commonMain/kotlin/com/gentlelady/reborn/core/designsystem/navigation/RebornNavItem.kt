package com.gentlelady.reborn.core.designsystem.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.ic_nav_home_default
import com.gentlelady.reborn.core.theme.RebornNavSelected
import com.gentlelady.reborn.core.theme.RebornNavUnselected

private val CommonIconSize = 24.dp

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
        label = { Text(label, fontSize = 11.sp) },
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
                    selectedIconColor = RebornNavSelected,
                    selectedTextColor = RebornNavSelected,
                    unselectedIconColor = RebornNavUnselected,
                    unselectedTextColor = RebornNavUnselected,
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
