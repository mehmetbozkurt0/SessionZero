package com.sessionzero.sessionzero.feature.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sessionzero.sessionzero.data.character.CharacterRepository
import com.sessionzero.sessionzero.navigation.CreationMethod
import com.sessionzero.sessionzero.ui.theme.accentColor

@Composable
fun DashboardScreen(
    characterRepository: CharacterRepository,
    isDarkMode: Boolean,
    onThemeToggle: () -> Unit,
    onNavigateToSystemSelection: (CreationMethod) -> Unit,
    onNavigateToCharacter: (Long) -> Unit,
    onNavigateToCharacterList: () -> Unit,
    viewModel: DashboardViewModel = viewModel { DashboardViewModel(characterRepository) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is DashboardContract.Effect.NavigateToSystemSelection -> onNavigateToSystemSelection(effect.method)
                is DashboardContract.Effect.NavigateToCharacterDetail -> onNavigateToCharacter(effect.characterId)
                DashboardContract.Effect.NavigateToCharacterList -> onNavigateToCharacterList()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .padding(horizontal = 24.dp),
    ) {
        Spacer(Modifier.height(48.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column {
                Text(
                    text = "SessionZero",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = "Choose your adventure",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            ThemeToggleButton(isDarkMode = isDarkMode, onClick = onThemeToggle)
        }

        Spacer(Modifier.height(32.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            DashboardActionCard(
                title = "Story AI",
                description = "Tell your character's story and let AI build the sheet for you.",
                glyph = "✦",
                onClick = { viewModel.onIntent(DashboardContract.Intent.StoryAiClicked) },
            )
            DashboardActionCard(
                title = "Guided Creation",
                description = "Answer a few questions to find the class that fits you best.",
                glyph = "→",
                onClick = { viewModel.onIntent(DashboardContract.Intent.GuidedCreationClicked) },
            )
            DashboardActionCard(
                title = "Blank Sheet",
                description = "Fill it out yourself — for experienced players who know the rules.",
                glyph = "✎",
                onClick = { viewModel.onIntent(DashboardContract.Intent.BlankSheetClicked) },
            )
        }

        Spacer(Modifier.height(40.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Recent Characters".uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (state.recentCharacters.isNotEmpty()) {
                Text(
                    text = "View All",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        viewModel.onIntent(DashboardContract.Intent.ViewAllClicked)
                    },
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        if (!state.isLoading && state.recentCharacters.isEmpty()) {
            Text(
                text = "No saved characters yet",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                state.recentCharacters.forEach { character ->
                    RecentCharacterCard(
                        character = character,
                        onClick = { viewModel.onIntent(DashboardContract.Intent.CharacterClicked(character.id)) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeToggleButton(isDarkMode: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.size(40.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        IconButton(onClick = onClick) {
            Text(
                text = if (isDarkMode) "☾" else "☀",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun DashboardActionCard(
    title: String,
    description: String,
    glyph: String,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.primary,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(36.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = glyph,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                )
            }
        }
    }
}

@Composable
private fun RecentCharacterCard(
    character: DashboardContract.RecentCharacter,
    onClick: () -> Unit,
) {
    val accent = character.classCategory?.accentColor ?: MaterialTheme.colorScheme.primary
    Surface(
        modifier = Modifier
            .width(140.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .height(4.dp)
                    .background(accent, shape = RoundedCornerShape(2.dp)),
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = character.name.ifBlank { "Unnamed Hero" },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 2,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = character.rpgSystem,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
