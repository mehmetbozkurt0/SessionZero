package com.sessionzero.sessionzero

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sessionzero.sessionzero.data.ai.AiRepository
import com.sessionzero.sessionzero.data.character.CharacterRepository
import com.sessionzero.sessionzero.data.dnd5e.DndClass
import com.sessionzero.sessionzero.feature.characterlist.CharacterListScreen
import com.sessionzero.sessionzero.feature.charactersheet.CharacterSheetScreen
import com.sessionzero.sessionzero.feature.dashboard.DashboardScreen
import com.sessionzero.sessionzero.feature.decisiontree.DecisionTreeScreen
import com.sessionzero.sessionzero.feature.storyai.StoryAiScreen
import com.sessionzero.sessionzero.feature.systemselection.SystemSelectionScreen
import com.sessionzero.sessionzero.navigation.AppDestination
import com.sessionzero.sessionzero.navigation.CreationMethod
import com.sessionzero.sessionzero.ui.theme.SessionZeroTheme
import com.sessionzero.sessionzero.ui.theme.SystemFlavor

@Composable
fun App(
    characterRepository: CharacterRepository,
    aiRepository: AiRepository,
) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val flavor = when (currentBackStackEntry?.destination?.route) {
        AppDestination.DASHBOARD, AppDestination.CHARACTER_LIST -> SystemFlavor.NEUTRAL
        else -> SystemFlavor.DND
    }
    val systemInDarkTheme = isSystemInDarkTheme()
    var isDarkMode by remember { mutableStateOf(systemInDarkTheme) }

    SessionZeroTheme(flavor = flavor, darkTheme = isDarkMode) {
        // Explicit root Surface so empty space (safe areas, system bars) also
        // picks up the active theme's background instead of staying light.
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            NavHost(
                navController = navController,
                startDestination = AppDestination.DASHBOARD,
            ) {
                composable(AppDestination.DASHBOARD) {
                    DashboardScreen(
                        characterRepository = characterRepository,
                        isDarkMode = isDarkMode,
                        onThemeToggle = { isDarkMode = !isDarkMode },
                        onNavigateToSystemSelection = { method ->
                            navController.navigate(AppDestination.systemSelectionRoute(method))
                        },
                        onNavigateToCharacter = { characterId ->
                            navController.navigate(AppDestination.characterDetailRoute(characterId))
                        },
                        onNavigateToCharacterList = {
                            navController.navigate(AppDestination.CHARACTER_LIST)
                        },
                    )
                }

                composable(AppDestination.CHARACTER_LIST) {
                    CharacterListScreen(
                        characterRepository = characterRepository,
                        onNavigateToCreateCharacter = {
                            navController.navigate(AppDestination.systemSelectionRoute(CreationMethod.GUIDED))
                        },
                        onNavigateToCharacter = { characterId ->
                            navController.navigate(AppDestination.characterDetailRoute(characterId))
                        },
                    )
                }

                composable(
                    route = AppDestination.SYSTEM_SELECTION,
                    arguments = listOf(
                        navArgument(AppDestination.ARG_CREATION_METHOD) { type = NavType.StringType }
                    ),
                ) { backStackEntry ->
                    val methodName = backStackEntry.arguments?.getString(AppDestination.ARG_CREATION_METHOD)
                    val creationMethod = CreationMethod.entries.find { it.name == methodName } ?: CreationMethod.GUIDED
                    SystemSelectionScreen(
                        creationMethod = creationMethod,
                        onNavigateToStoryAi = {
                            navController.navigate(AppDestination.STORY_AI)
                        },
                        onNavigateToDecisionTree = {
                            navController.navigate(AppDestination.DECISION_TREE)
                        },
                        onNavigateToBlankSheet = { dndClass ->
                            navController.navigate(AppDestination.characterSheetRoute(dndClass.id, isBlank = true))
                        },
                    )
                }

                composable(AppDestination.STORY_AI) {
                    StoryAiScreen(
                        aiRepository = aiRepository,
                        characterRepository = characterRepository,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToCharacterDetail = { characterId ->
                            navController.navigate(AppDestination.characterDetailRoute(characterId)) {
                                popUpTo(AppDestination.DASHBOARD) { inclusive = false }
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
                        navArgument(AppDestination.ARG_CLASS_ID) { type = NavType.StringType },
                        navArgument(AppDestination.ARG_IS_BLANK) { type = NavType.BoolType; defaultValue = false },
                    ),
                ) { backStackEntry ->
                    val classId = backStackEntry.arguments?.getString(AppDestination.ARG_CLASS_ID)
                    val dndClass = classId?.let { DndClass.fromId(it) } ?: return@composable
                    val isBlank = backStackEntry.arguments?.getBoolean(AppDestination.ARG_IS_BLANK) ?: false
                    CharacterSheetScreen(
                        dndClass = dndClass,
                        characterId = null,
                        isBlankSheet = isBlank,
                        characterRepository = characterRepository,
                        onNavigateToSystemSelection = {
                            navController.navigate(AppDestination.DASHBOARD) {
                                popUpTo(AppDestination.DASHBOARD) { inclusive = true }
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
}
