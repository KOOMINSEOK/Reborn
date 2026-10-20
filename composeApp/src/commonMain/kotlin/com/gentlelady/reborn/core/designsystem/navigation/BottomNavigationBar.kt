package com.gentlelady.reborn.core.designsystem.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.*

import com.gentlelady.reborn.core.theme.RebornDividerGray
import com.gentlelady.reborn.core.theme.RebornNavSelected
import com.gentlelady.reborn.core.theme.RebornNavUnselected

@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    Column {
        HorizontalDivider(thickness = 1.dp, color = RebornDividerGray)
        NavigationBar(
            containerColor = Color.White,
            tonalElevation = 0.dp
        ) {
            val navItemColors = NavigationBarItemDefaults.colors(
                selectedIconColor = RebornNavSelected,
                selectedTextColor = RebornNavSelected,
                unselectedIconColor = RebornNavUnselected,
                unselectedTextColor = RebornNavUnselected,
                indicatorColor = Color.Transparent
            )

            RebornNavItem(Res.drawable.ic_nav_home_default, "홈", currentRoute == "home", navItemColors) { onNavigate("home") }
            RebornNavItem(
                Res.drawable.ic_flower,
                "메모리얼",
                currentRoute == "memorial/me" || currentRoute == "wreath/checkout/{tier}" || currentRoute == "wreath/message/{tier}",
                navItemColors
            ) { onNavigate("memorial/me") }
            RebornNavItem(Res.drawable.ic_nav_search_default, "검색", currentRoute == "search", navItemColors) { onNavigate("search") }
            RebornNavItem(Res.drawable.ic_nav_message_default, "메시지", currentRoute == "message", navItemColors) { onNavigate("message") }
            RebornNavItem(Res.drawable.ic_nav_profile_default, "프로필", currentRoute == "profile", navItemColors) { onNavigate("profile") }
        }
    }
}

@Preview(showBackground = true, name = "BottomNavigationBar Whole Preview")
@Composable
fun BottomBarHomePreview() {
    MaterialTheme {
        Surface {
            BottomNavigationBar(currentRoute = "home", onNavigate = {})
        }
    }
}
