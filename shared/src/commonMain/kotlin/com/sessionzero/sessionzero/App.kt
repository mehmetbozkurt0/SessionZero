package com.sessionzero.sessionzero

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sessionzero.sessionzero.data.dnd5e.DndClass
import com.sessionzero.sessionzero.feature.charactersheet.CharacterSheetScreen
import com.sessionzero.sessionzero.feature.decisiontree.DecisionTreeScreen
import com.sessionzero.sessionzero.feature.systemselection.SystemSelectionScreen
import com.sessionzero.sessionzero.navigation.AppDestination
import com.sessionzero.sessionzero.ui.theme.SessionZeroTheme

@Composable
fun App() {
    SessionZeroTheme {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = AppDestination.SYSTEM_SELECTION,
        ) {
            composable(AppDestination.SYSTEM_SELECTION) {
                SystemSelectionScreen(
                    onNavigateToDecisionTree = {
                        navController.navigate(AppDestination.DECISION_TREE)
                    },
                )
            }

            composable(AppDestination.DECISION_TREE) {
                DecisionTreeScreen(
                    onNavigateToCharacterSheet = { dndClass ->
                        navController.navigate(AppDestination.characterSheetRoute(dndClass.id))
                    },
                    onNavigateBack = { navController.popBackStack() },
                )
            }

            composable(
                route = AppDestination.CHARACTER_SHEET,
                arguments = listOf(
                    navArgument(AppDestination.ARG_CLASS_ID) { type = NavType.StringType }
                ),
            ) { backStackEntry ->
                val classId = backStackEntry.arguments?.getString(AppDestination.ARG_CLASS_ID)
                val dndClass = classId?.let { DndClass.fromId(it) } ?: return@composable
                CharacterSheetScreen(
                    dndClass = dndClass,
                    onNavigateToSystemSelection = {
                        navController.navigate(AppDestination.SYSTEM_SELECTION) {
                            popUpTo(AppDestination.SYSTEM_SELECTION) { inclusive = true }
                        }
                    },
                )
            }
        }
    }
}
