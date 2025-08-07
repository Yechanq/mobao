package com.example.mobao.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mobao.ui.screens.first.FirstScreen
import com.example.mobao.ui.screens.first.PostDetailScreen
import com.example.mobao.ui.screens.forth.ForthScreen
import com.example.mobao.ui.screens.first.PostScreen
import com.example.mobao.ui.screens.second.SecondScreen
import com.example.mobao.ui.screens.third.ThirdScreen
import com.example.mobao.ui.screens.forth.ManualInputScreen
import com.example.mobao.ui.screens.forth.ForthMainScreen
import com.example.mobao.ui.screens.forth.ForthMainViewModel
import com.example.mobao.ui.screens.forth.MedicineDetailScreen
import com.example.mobao.ui.screens.forth.MedicineDetailViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mobao.ui.screens.forth.AddMedicineScreen


@Composable
fun AppNavigation(navController: NavHostController) {

    NavHost(navController = navController, startDestination = "first") {

        composable("first") {
            FirstScreen(navController = navController)
        }
        composable("second") {
            SecondScreen()
        }
        composable("third") {
            ThirdScreen()
        }
        composable("forthMain") {
            val vm: ForthMainViewModel = hiltViewModel()
            ForthMainScreen(navController = navController, viewModel = vm)
        }
        composable("addMedicine") {
            AddMedicineScreen(navController = navController)
        }
        composable("manualInput") {
            ManualInputScreen(navController = navController)
        }
        composable(
            route = "medicineDetail/{medicineId}",
            arguments = listOf(navArgument("medicineId") {
                type = NavType.LongType
            })
        ) {
            val vm: MedicineDetailViewModel = hiltViewModel()
            MedicineDetailScreen(
                navController = navController,
                viewModel = vm
            )
        }
        composable("post") {
            PostScreen(navController = navController)
        }
        // 👇 게시글 상세 화면 라우트 추가
        composable(
            "postDetail/{postId}",
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId")
            if (postId != null) {
                PostDetailScreen(postId = postId)
            }
        }
    }
}