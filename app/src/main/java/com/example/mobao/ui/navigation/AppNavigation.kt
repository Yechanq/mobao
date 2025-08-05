package com.example.mobao.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mobao.ui.screens.first.FirstScreen
import com.example.mobao.ui.screens.forth.ForthScreen
import com.example.mobao.ui.screens.second.SecondScreen
import com.example.mobao.ui.screens.third.ThirdScreen
import com.example.mobao.ui.screens.forth.ManualInputScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobao.ui.screens.forth.ForthViewModel
import com.example.mobao.ui.screens.forth.MainScreen
import com.example.mobao.ui.screens.forth.MainViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "first") {
        composable("first") {
            FirstScreen()
        }
        composable("second") {
            SecondScreen()
        }
        composable("third") {
            ThirdScreen()
        }
        composable("main") {
            val mainViewModel: MainViewModel = viewModel()
            MainScreen(navController = navController, viewModel = mainViewModel)
        }
        composable("forth") {
            ForthScreen(navController = navController)
        }
        composable("manualInput") {
            val forthViewModel: ForthViewModel = hiltViewModel()
            ManualInputScreen(navController = navController, viewModel = forthViewModel)
        }
    }
}

