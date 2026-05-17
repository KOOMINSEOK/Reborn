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

import com.gentlelady.reborn.core.theme.RebornDeepBlue
import com.gentlelady.reborn.core.theme.RebornUnselectedGray

@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val commonIconSize = 28.dp

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp,
        modifier = Modifier.height(84.dp)
    ) {
        val navItemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = RebornDeepBlue,       // 변경
            selectedTextColor = RebornDeepBlue,       // 변경
            unselectedIconColor = RebornUnselectedGray, // 변경
            unselectedTextColor = RebornUnselectedGray, // 변경
            indicatorColor = Color.Transparent
        )

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
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .background(
                                    color = if (isSelected) RebornDeepBlue else Color.Transparent, // 변경
                                    shape = CircleShape
                                )
                        )
                    }
                },
                colors = navItemColors
            )
        }

        RebornNavItem("home", Res.drawable.ic_nav_home_default, "Home")
        RebornNavItem("search", Res.drawable.ic_nav_search_default, "Search")

        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier.weight(1f).fillMaxHeight()
        ) {
            FloatingActionButton(
                onClick = { },
                shape = CircleShape,
                containerColor = RebornDeepBlue, // 변경
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(4.dp, 8.dp),
                modifier = Modifier
                    .size(54.dp)
                    .border(width = 4.dp, color = Color.White, shape = CircleShape)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(24.dp))
            }
        }

        RebornNavItem("message", Res.drawable.ic_nav_message_default, "Message")
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