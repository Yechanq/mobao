package com.example.mobao.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mobao.ui.screens.first.FirstScreen
import com.example.mobao.ui.screens.second.SecondScreen
import com.example.mobao.ui.screens.third.ThirdScreen
import com.example.mobao.ui.screens.forth.ManualInputScreen
import com.example.mobao.ui.screens.forth.ForthMainScreen
import com.example.mobao.ui.screens.forth.ForthMainViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mobao.ui.screens.forth.AddMedicineScreen


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
    }
}

