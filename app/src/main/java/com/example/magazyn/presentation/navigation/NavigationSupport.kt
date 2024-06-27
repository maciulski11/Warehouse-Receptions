package com.example.magazyn.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.magazyn.presentation.screen.AddDocument.AddDocumentViewModel
import com.example.magazyn.presentation.screen.Contractor.ContractorsViewModel
import com.example.magazyn.presentation.screen.Document.DocumentsViewModel
import com.example.magazyn.presentation.screen.EditDocument.EditDocumentViewModel
import com.example.magazyn.data.FirebaseService
import com.example.magazyn.presentation.screen.AddDocument.AddDocumentScreen
import com.example.magazyn.presentation.screen.Contractor.ContractorsScreen
import com.example.magazyn.presentation.screen.Document.DocumentScreen
import com.example.magazyn.presentation.screen.EditDocument.EditDocumentScreen
import com.example.magazyn.presentation.screen.MainScreen

object NavigationSupport {
    const val ContractorScreen = "contractor_screen"
    const val DocumentsScreen = "documents_screen"
    const val AddDocumentScreen = "add_document_screen"
    const val EditDocumentScreen = "edit_document_screen"
    const val MainScreen = "main_screen"
}

@Composable
fun NavigationScreens(firebaseService: FirebaseService) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavigationSupport.MainScreen
    ) {

        composable(NavigationSupport.MainScreen) {

            MainScreen(navController)
        }

        composable(NavigationSupport.ContractorScreen) {

            val viewModel = remember {
                ContractorsViewModel(firebaseService)
            }

            ContractorsScreen(navController, viewModel)
        }

        composable(NavigationSupport.DocumentsScreen) {

            val viewModel = remember {
                DocumentsViewModel(firebaseService)
            }

            DocumentScreen(navController, viewModel)
        }

        composable(NavigationSupport.AddDocumentScreen) {

            val viewModel = remember {
                AddDocumentViewModel(firebaseService)
            }

            val viewModel2 = remember {
                ContractorsViewModel(firebaseService)
            }

            AddDocumentScreen(navController, viewModel, viewModel2)
        }

        composable("${NavigationSupport.EditDocumentScreen}/{uid}/{contractorUid}",
            arguments = listOf(
                navArgument("uid") { type = NavType.StringType },
                navArgument("contractorUid") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            val contractorUid = backStackEntry.arguments?.getString("contractorUid") ?: ""

            val viewModel = remember {
                EditDocumentViewModel(firebaseService, uid, contractorUid)
            }

            val viewModel2 = remember {
                AddDocumentViewModel(firebaseService)
            }

            val viewModel3 = remember {
                ContractorsViewModel(firebaseService)
            }

            EditDocumentScreen(navController, viewModel, viewModel3, viewModel2)
        }
    }
}