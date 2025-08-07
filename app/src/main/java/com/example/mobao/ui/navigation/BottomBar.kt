package com.example.mobao.ui.navigation

import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem("Home", Icons.Default.Home, "forthMain"),
        BottomNavItem("Add",  Icons.Default.Add,  "addMedicine")
        // 필요에 따라 더 추가 가능 (예: second, third 등)
    )
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // startDestinationId까지는 popUp, 스택 너무 커지는 걸 방지
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            // 하나만 쌓이도록 방지
                            launchSingleTop = true
                            // 이전 상태 복원
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
