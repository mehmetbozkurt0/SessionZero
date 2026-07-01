package com.sessionzero.sessionzero

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sessionzero.sessionzero.data.ai.AiRepository
import com.sessionzero.sessionzero.data.character.CharacterRepository
import com.sessionzero.sessionzero.data.dnd5e.DndClass
import com.sessionzero.sessionzero.feature.characterlist.CharacterListScreen
import com.sessionzero.sessionzero.feature.charactersheet.CharacterSheetScreen
import com.sessionzero.sessionzero.feature.creationmode.CreationModeScreen
import com.sessionzero.sessionzero.feature.decisiontree.DecisionTreeScreen
import com.sessionzero.sessionzero.feature.storyai.StoryAiScreen
import com.sessionzero.sessionzero.feature.systemselection.SystemSelectionScreen
import com.sessionzero.sessionzero.navigation.AppDestination
import com.sessionzero.sessionzero.ui.theme.SessionZeroTheme

@Composable
fun App(
    characterRepository: CharacterRepository,
    aiRepository: AiRepository,
) {
    SessionZeroTheme {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = AppDestination.CHARACTER_LIST,
        ) {
            composable(AppDestination.CHARACTER_LIST) {
                CharacterListScreen(
                    characterRepository = characterRepository,
                    onNavigateToCreateCharacter = {
                        navController.navigate(AppDestination.SYSTEM_SELECTION)
                    },
                    onNavigateToCharacter = { characterId ->
                        navController.navigate(AppDestination.characterDetailRoute(characterId))
                    },
                )
            }

            composable(AppDestination.SYSTEM_SELECTION) {
                SystemSelectionScreen(
                    onNavigateToDecisionTree = {
                        navController.navigate(AppDestination.CREATION_MODE)
                    },
                )
            }

            composable(AppDestination.CREATION_MODE) {
                CreationModeScreen(
                    onNavigateToStoryAi = {
                        navController.navigate(AppDestination.STORY_AI)
                    },
                    onNavigateToDecisionTree = {
                        navController.navigate(AppDestination.DECISION_TREE)
                    },
                    onNavigateBack = { navController.popBackStack() },
                )
            }

            composable(AppDestination.STORY_AI) {
                StoryAiScreen(
                    aiRepository = aiRepository,
                    characterRepository = characterRepository,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToCharacterDetail = { characterId ->
                        navController.navigate(AppDestination.characterDetailRoute(characterId)) {
                            popUpTo(AppDestination.CHARACTER_LIST) { inclusive = false }
                        }
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
                    characterId = null,
                    characterRepository = characterRepository,
                    onNavigateToSystemSelection = {
                        navController.navigate(AppDestination.CHARACTER_LIST) {
                            popUpTo(AppDestination.CHARACTER_LIST) { inclusive = true }
                        }
                    },
                )
            }

            composable(
                route = AppDestination.CHARACTER_DETAIL,
                arguments = listOf(
                    navArgument(AppDestination.ARG_CHARACTER_ID) { type = NavType.LongType }
                ),
            ) { backStackEntry ->
                val characterId = backStackEntry.arguments?.getLong(AppDestination.ARG_CHARACTER_ID)
                    ?: return@composable
                CharacterSheetScreen(
                    dndClass = null,
                    characterId = characterId,
                    characterRepository = characterRepository,
                    onNavigateToSystemSelection = {
                        navController.popBackStack()
                    },
                )
            }
        }
    }
}
