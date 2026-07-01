package com.sessionzero.sessionzero.feature.characterlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sessionzero.sessionzero.data.character.CharacterRepository
import com.sessionzero.sessionzero.data.dnd5e.ClassCategory
import com.sessionzero.sessionzero.ui.theme.accentColor

@Composable
fun CharacterListScreen(
    characterRepository: CharacterRepository,
    onNavigateToCreateCharacter: () -> Unit,
    onNavigateToCharacter: (Long) -> Unit,
    viewModel: CharacterListViewModel = viewModel { CharacterListViewModel(characterRepository) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CharacterListContract.Effect.NavigateToCharacterDetail -> {
                    onNavigateToCharacter(effect.characterId)
                }
                CharacterListContract.Effect.NavigateToCreateCharacter -> {
                    onNavigateToCreateCharacter()
                }
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onIntent(CharacterListContract.Intent.CreateCharacterClicked) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Text(
                    text = "+",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Light,
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
        ) {
            Spacer(Modifier.height(48.dp))

            Text(
                text = "My Characters",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = "Your saved adventurers",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )

            Spacer(Modifier.height(32.dp))

            if (!state.isLoading && state.characters.isEmpty()) {
                EmptyState(modifier = Modifier.fillMaxSize())
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(state.characters, key = { it.id }) { item ->
                        CharacterCard(
                            item = item,
                            onClick = { viewModel.onIntent(CharacterListContract.Intent.CharacterClicked(item)) },
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun CharacterCard(
    item: CharacterListContract.CharacterListItem,
    onClick: () -> Unit,
) {
    val accent = item.classCategory?.accentColor ?: MaterialTheme.colorScheme.primary

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(64.dp)
                    .background(
                        color = accent,
                        shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp),
                    ),
            )

            Spacer(Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 14.dp),
            ) {
                Text(
                    text = item.name.ifBlank { "Unnamed Hero" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(Modifier.height(4.dp))
                SystemBadge(system = item.rpgSystem, accent = accent)
            }

            Spacer(Modifier.width(16.dp))
        }
    }
}

@Composable
private fun SystemBadge(system: String, accent: Color) {
    Box(
        modifier = Modifier
            .background(
                color = accent.copy(alpha = 0.12f),
                shape = RoundedCornerShape(4.dp),
            )
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(
            text = system,
            style = MaterialTheme.typography.labelSmall,
            color = accent,
        )
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(36.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "⚔",
                style = MaterialTheme.typography.headlineLarge,
            )
        }
        Spacer(Modifier.height(20.dp))
        Text(
            text = "No saved characters yet",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Tap the + button below\nto create a new hero",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
