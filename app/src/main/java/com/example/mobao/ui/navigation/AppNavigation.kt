package com.example.mobao.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mobao.ui.screens.first.FirstScreen
import com.example.mobao.ui.screens.forth.ForthScreen
import com.example.mobao.ui.screens.post.PostScreen // PostScreen import 확인
import com.example.mobao.ui.screens.second.SecondScreen
import com.example.mobao.ui.screens.third.ThirdScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "first") {
        composable("first") {
            // FirstScreen에 navController를 전달하여 화면 전환이 가능하게 함
            FirstScreen(navController = navController)
        }
        composable("second") {
            SecondScreen()
        }
        composable("third") {
            ThirdScreen()
        }
        composable("forth") {
            ForthScreen()
        }
        // 이 부분이 가장 중요합니다! "post" 라우트가 정의되어 있는지 확인하세요.
        composable("post") {
            PostScreen(navController = navController)
        }
    }
}