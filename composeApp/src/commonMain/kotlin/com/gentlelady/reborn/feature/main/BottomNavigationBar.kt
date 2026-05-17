package com.gentlelady.reborn.feature.main

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
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.* import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

// 선택 시 사용할 Cobalt Blue 색상 정의
val SelectedBlue = Color(0xFF0047AB)
val UnselectedGray = Color(0xFF9E9E9E)

@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val commonIconSize = 28.dp

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp,
        // 점(Dot) 공간이 추가되므로 기존보다 높이를 살짝 키워 84dp로 지정합니다.
        modifier = Modifier.height(84.dp)
    ) {
        val navItemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = SelectedBlue,
            selectedTextColor = SelectedBlue,
            unselectedIconColor = UnselectedGray,
            unselectedTextColor = UnselectedGray,
            indicatorColor = Color.Transparent
        )

        // 공통 네비게이션 아이템을 그려주는 헬퍼 람다 컴포넌트
        @Composable
        fun RowScope.RebornNavItem(
            route: String,
            iconRes: org.jetbrains.compose.resources.DrawableResource,
            label: String
        ) {
            val isSelected = currentRoute == route

            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(route) },
                icon = {
                    Icon(
                        painter = painterResource(iconRes),
                        contentDescription = label,
                        modifier = Modifier.size(commonIconSize)
                    )
                },
                label = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(label)
                        Spacer(modifier = Modifier.height(4.dp))
                        // 선택되었을 때만 점 색상을 노출 (비선택 시 투명하게 공간을 유지하여 UI 덜컹거림 방지)
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .background(
                                    color = if (isSelected) SelectedBlue else Color.Transparent,
                                    shape = CircleShape
                                )
                        )
                    }
                },
                colors = navItemColors
            )
        }

        // 1. Home
        RebornNavItem("home", Res.drawable.ic_nav_home_default, "Home")

        // 2. Search
        RebornNavItem("search", Res.drawable.ic_nav_search_default, "Search")

        // 3. 중앙 플러스 버튼 (상단 경계선 일치 구조)
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            FloatingActionButton(
                onClick = { /* 게시글 작성 화면 등으로 이동 */ },
                shape = CircleShape,
                containerColor = SelectedBlue,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp
                ),
                modifier = Modifier
                    .size(54.dp)
                    .border(
                        width = 4.dp,
                        color = Color.White,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // 4. Message
        RebornNavItem("message", Res.drawable.ic_nav_message_default, "Message")

        // 5. Profile
        RebornNavItem("profile", Res.drawable.ic_nav_profile_default, "Profile")
    }
}

@Preview(showBackground = true, name = "Home Tab Selected")
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