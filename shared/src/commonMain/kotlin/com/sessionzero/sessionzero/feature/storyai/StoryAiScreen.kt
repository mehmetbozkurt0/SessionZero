package com.sessionzero.sessionzero.feature.storyai

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sessionzero.sessionzero.data.ai.AiRepository
import com.sessionzero.sessionzero.data.character.CharacterRepository

@Composable
fun StoryAiScreen(
    aiRepository: AiRepository,
    characterRepository: CharacterRepository,
    onNavigateBack: () -> Unit,
    onNavigateToCharacterDetail: (Long) -> Unit,
    viewModel: StoryAiViewModel = viewModel { StoryAiViewModel(aiRepository, characterRepository) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                StoryAiContract.Effect.NavigateBack -> onNavigateBack()
                is StoryAiContract.Effect.NavigateToCharacterDetail ->
                    onNavigateToCharacterDetail(effect.characterId)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
    ) {
        Spacer(Modifier.height(48.dp))

        Text(
            text = "Karakterinin hikayesini anlat",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Motivasyonunu, geçmişini ya da kişiliğini yaz. Yapay zeka sana en uygun D&D 5E sınıfını önerecek.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = state.story,
            onValueChange = { viewModel.onIntent(StoryAiContract.Intent.StoryChanged(it)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            placeholder = {
                Text(
                    text = "Örnek: \"Ormanda büyüyen, ata ruhlarıyla konuşabilen bir şamandım. Köyüm yakılınca intikam yemini ettim...\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            shape = MaterialTheme.shapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary,
            ),
            enabled = !state.isAnalyzing,
        )

        if (state.errorMessage != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = state.errorMessage!!,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { viewModel.onIntent(StoryAiContract.Intent.AnalyzeClicked) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isAnalyzing,
        ) {
            if (state.isAnalyzing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
                Spacer(Modifier.size(10.dp))
                Text("Analiz ediliyor...")
            } else {
                Text(
                    text = "Karakterimi Analiz Et",
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        TextButton(
            onClick = { viewModel.onIntent(StoryAiContract.Intent.BackPressed) },
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Text(
                text = "Geri",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(Modifier.height(32.dp))
    }
}
