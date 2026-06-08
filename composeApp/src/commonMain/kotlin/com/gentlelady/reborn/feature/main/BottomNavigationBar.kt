package com.gentlelady.reborn.feature.main

import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.*

// 분리된 하위 컴포넌트 임포트
import com.gentlelady.reborn.feature.main.components.RebornNavItem
import com.gentlelady.reborn.feature.main.components.CentralAddButton

import com.gentlelady.reborn.core.theme.RebornDeepBlue
import com.gentlelady.reborn.core.theme.RebornUnselectedGray

@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp,
        modifier = Modifier.height(84.dp)
    ) {
        val navItemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = RebornDeepBlue,
            selectedTextColor = RebornDeepBlue,
            unselectedIconColor = RebornUnselectedGray,
            unselectedTextColor = RebornUnselectedGray,
            indicatorColor = Color.Transparent
        )

        // 왼쪽 영역
        RebornNavItem(Res.drawable.ic_nav_home_default, "Home", currentRoute == "home", navItemColors) { onNavigate("home") }
        RebornNavItem(Res.drawable.ic_nav_search_default, "Search", currentRoute == "search", navItemColors) { onNavigate("search") }

        // 중앙 영역
        CentralAddButton(onClick = { /* 게시글 작성 동작 */ })

        // 오른쪽 영역
        RebornNavItem(Res.drawable.ic_nav_message_default, "Message", currentRoute == "message", navItemColors) { onNavigate("message") }
        RebornNavItem(Res.drawable.ic_nav_profile_default, "Profile", currentRoute == "profile", navItemColors) { onNavigate("profile") }
    }
}

@Preview(showBackground = true, name = "BottomNavigationBar Whole Preview")
@Composable
fun BottomBarHomePreview() {
    MaterialTheme {
        Surface {
            BottomNavigationBar(
                currentRoute = "home",
                onNavigate = {}
            )
        }
    }
}