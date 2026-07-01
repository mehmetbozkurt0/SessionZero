package com.sessionzero.sessionzero.feature.creationmode

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CreationModeScreen(
    onNavigateToStoryAi: () -> Unit,
    onNavigateToDecisionTree: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: CreationModeViewModel = viewModel { CreationModeViewModel() },
) {
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                CreationModeContract.Effect.NavigateToStoryAi -> onNavigateToStoryAi()
                CreationModeContract.Effect.NavigateToDecisionTree -> onNavigateToDecisionTree()
                CreationModeContract.Effect.NavigateBack -> onNavigateBack()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Karakter nasıl oluşturmak istersin?",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Sınıfını kendin seçebilir ya da hikayeni anlatıp yapay zekaya bırakabilirsin.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(40.dp))

        ModeCard(
            title = "Hikayeden Üret",
            badge = "Yapay Zeka",
            description = "Karakterinin geçmişini, motivasyonunu veya kişiliğini anlat. Yapay zeka sana en uygun sınıfı önersin.",
            onClick = { viewModel.onIntent(CreationModeContract.Intent.AiModeSelected) },
        )

        Spacer(Modifier.height(16.dp))

        ModeCard(
            title = "Kendim Seçeceğim",
            badge = "Manuel",
            description = "Birkaç soruya yanıt vererek D&D 5E sınıfını kendin belirle. Hızlı ve doğrudan.",
            onClick = { viewModel.onIntent(CreationModeContract.Intent.ManualModeSelected) },
        )

        Spacer(Modifier.height(32.dp))

        TextButton(
            onClick = { viewModel.onIntent(CreationModeContract.Intent.BackPressed) },
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Text(
                text = "Geri",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ModeCard(
    title: String,
    badge: String,
    description: String,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.large,
            )
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Surface(
                    shape = MaterialTheme.shapes.extraSmall,
                    color = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
