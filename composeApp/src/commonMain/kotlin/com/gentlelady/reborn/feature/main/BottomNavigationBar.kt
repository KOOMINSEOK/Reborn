package com.gentlelady.reborn.feature.main

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
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.* // 모든 아이콘 리소스 포함
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.gentlelady.reborn.ic_nav_home_default
import com.gentlelady.reborn.ic_nav_search_default
import com.gentlelady.reborn.ic_nav_message_default
import com.gentlelady.reborn.ic_nav_profile_default


// 선택 시 사용할 Cobalt Blue 색상 정의
val SelectedBlue = Color(0xFF0047AB)
val UnselectedGray = Color(0xFF9E9E9E) // 비활성 상태용 그레이

@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val commonIconSize = 28.dp

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp
    ) {
        // 공통 컬러 설정 (아이콘과 텍스트 모두 적용)
        val navItemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = SelectedBlue,
            selectedTextColor = SelectedBlue,
            unselectedIconColor = UnselectedGray,
            unselectedTextColor = UnselectedGray,
            indicatorColor = Color.Transparent // 선택 시 나타나는 배경 원(Pill) 제거
        )

        // 1. Home
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = { onNavigate("home") },
            icon = {
                Icon(
                    painter = painterResource(Res.drawable.ic_nav_home_default),
                    contentDescription = "Home",
                    modifier = Modifier.size(commonIconSize)
                )
            },
            label = { Text("Home") },
            colors = navItemColors
        )

        // 2. Search
        NavigationBarItem(
            selected = currentRoute == "search",
            onClick = { onNavigate("search") },
            icon = {
                Icon(
                    painter = painterResource(Res.drawable.ic_nav_search_default),
                    contentDescription = "Search",
                    modifier = Modifier.size(commonIconSize)
                )
            },
            label = { Text("Search") },
            colors = navItemColors
        )

        // 3. 중앙 플러스 버튼
        Box(contentAlignment = Alignment.Center, modifier = Modifier.weight(1f)) {
            FloatingActionButton(
                onClick = { /* 게시글 작성 화면 등으로 이동 */ },
                shape = CircleShape,
                containerColor = SelectedBlue, // 플러스 버튼도 동일한 블루 적용
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(0.dp),
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }

        // 4. Message
        NavigationBarItem(
            selected = currentRoute == "message",
            onClick = { onNavigate("message") },
            icon = {
                Icon(
                    painter = painterResource(Res.drawable.ic_nav_message_default),
                    contentDescription = "Message",
                    modifier = Modifier.size(commonIconSize)
                )
            },
            label = { Text("Message") },
            colors = navItemColors
        )

        // 5. Profile
        NavigationBarItem(
            selected = currentRoute == "profile",
            onClick = { onNavigate("profile") },
            icon = {
                Icon(
                    painter = painterResource(Res.drawable.ic_nav_profile_default),
                    contentDescription = "Profile",
                    modifier = Modifier.size(commonIconSize)
                )
            },
            label = { Text("Profile") },
            colors = navItemColors
        )
    }
}
@Preview(showBackground = true, name = "Home Tab Selected")
@Composable
fun BottomBarHomePreview() {
    MaterialTheme {
        // 배경색을 확인하기 위해 Surface로 감쌉니다.
        Surface {
            BottomNavigationBar(
                currentRoute = "home",
                onNavigate = {}
            )
        }
    }
}