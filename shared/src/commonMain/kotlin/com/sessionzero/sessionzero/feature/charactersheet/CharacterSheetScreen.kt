package com.sessionzero.sessionzero.feature.charactersheet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sessionzero.sessionzero.data.character.CharacterRepository
import com.sessionzero.sessionzero.data.dnd5e.DndClass
import kotlinx.coroutines.delay
import com.sessionzero.sessionzero.ui.theme.LocalSessionZeroAccent
import com.sessionzero.sessionzero.ui.theme.accentColor

// — Ekran girişi —

@Composable
fun CharacterSheetScreen(
    dndClass: DndClass,
    characterRepository: CharacterRepository,
    viewModel: CharacterSheetViewModel = viewModel { CharacterSheetViewModel(dndClass, characterRepository) },
    onNavigateToSystemSelection: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showSavedMessage by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                CharacterSheetContract.Effect.NavigateToSystemSelection -> onNavigateToSystemSelection()
                CharacterSheetContract.Effect.ShowSaveSuccess -> {
                    showSavedMessage = true
                    delay(2500)
                    showSavedMessage = false
                }
            }
        }
    }

    CompositionLocalProvider(LocalSessionZeroAccent provides dndClass.category.accentColor) {
        CharacterSheetContent(
            state = state,
            showSavedMessage = showSavedMessage,
            onIntent = viewModel::onIntent,
        )
    }
}

// — Ana içerik —

@Composable
private fun CharacterSheetContent(
    state: CharacterSheetContract.State,
    showSavedMessage: Boolean,
    onIntent: (CharacterSheetContract.Intent) -> Unit,
) {
    val accent = LocalSessionZeroAccent.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
    ) {
        Spacer(Modifier.height(48.dp))

        ClassHeader(dndClass = state.dndClass, accent = accent)

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = state.characterName,
            onValueChange = { onIntent(CharacterSheetContract.Intent.UpdateName(it)) },
            label = { Text("Karakter Adı") },
            placeholder = { Text("Kahramanını adlandır...") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = accent,
                focusedLabelColor = accent,
                cursorColor = accent,
            ),
        )

        Spacer(Modifier.height(36.dp))
        SectionHeader(title = "Savaş İstatistikleri", accent = accent)
        Spacer(Modifier.height(12.dp))
        CombatStatsRow(stats = state.combatStats, accent = accent)

        Spacer(Modifier.height(28.dp))
        SectionHeader(title = "Temel Nitelikler", accent = accent)
        Spacer(Modifier.height(12.dp))
        AbilityScoresGrid(scores = state.abilityScores, accent = accent)

        Spacer(Modifier.height(28.dp))
        SectionHeader(title = "Can Puanı", accent = accent)
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StatLabel("Seviye")
            StatValue("${state.level}")
        }
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 10.dp),
            color = MaterialTheme.colorScheme.outline,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StatLabel("Temel HP (1. Seviye)")
            StatValue("${state.baseHp} + Anayasa mod.")
        }

        Spacer(Modifier.height(28.dp))
        SectionHeader(title = "Temel Saldırılar ve Büyüler", accent = accent)
        Spacer(Modifier.height(12.dp))
        ActionsTable(actions = state.actions)

        Spacer(Modifier.height(28.dp))
        SectionHeader(title = "Başlangıç Ekipmanı", accent = accent)
        Spacer(Modifier.height(12.dp))
        state.dndClass.startingEquipment.forEach { item ->
            EquipmentRow(item = item, accent = accent)
            Spacer(Modifier.height(6.dp))
        }

        Spacer(Modifier.height(28.dp))
        SectionHeader(title = "Karakter Geçmişi", accent = accent)
        Spacer(Modifier.height(8.dp))
        Text(
            text = state.backstory,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(48.dp))

        Button(
            onClick = { onIntent(CharacterSheetContract.Intent.SaveCharacter) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = accent),
        ) {
            Text(
                text = "Karakteri Kaydet",
                style = MaterialTheme.typography.labelLarge,
            )
        }

        AnimatedVisibility(
            visible = showSavedMessage,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Text(
                text = "✓ Karakter başarıyla kaydedildi",
                style = MaterialTheme.typography.bodySmall,
                color = accent,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                textAlign = TextAlign.Center,
            )
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = { onIntent(CharacterSheetContract.Intent.StartOver) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
        ) {
            Text(
                text = "Baştan Başla",
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

// — Bölüm bileşenleri —

@Composable
private fun ClassHeader(dndClass: DndClass, accent: Color) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(68.dp)
                .background(accent, shape = RoundedCornerShape(2.dp)),
        )
        Spacer(Modifier.width(16.dp))
        Column {
            Text(
                text = "Karakter Kağıdı",
                style = MaterialTheme.typography.labelLarge,
                color = accent,
            )
            Text(
                text = dndClass.displayName,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = dndClass.flavor,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String, accent: Color) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = accent,
    )
    Spacer(Modifier.height(4.dp))
    HorizontalDivider(color = accent.copy(alpha = 0.25f))
}

@Composable
private fun CombatStatsRow(stats: CombatStats, accent: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        CombatStatCard(label = "Zırh Sınıfı", value = "${stats.armorClass}", modifier = Modifier.weight(1f))
        CombatStatCard(label = "İnisiyatif", value = stats.initiative.toSignedString(), modifier = Modifier.weight(1f))
        CombatStatCard(label = "Hız", value = "${stats.speedFt} ft", modifier = Modifier.weight(1f))
    }
}

@Composable
private fun CombatStatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun AbilityScoresGrid(scores: List<AbilityScore>, accent: Color) {
    val rows = scores.chunked(3)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        rows.forEach { rowScores ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                rowScores.forEach { score ->
                    AbilityScoreCard(score = score, accent = accent, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun AbilityScoreCard(score: AbilityScore, accent: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = score.abbreviation,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = score.modifier.toSignedString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = accent,
            )
            Text(
                text = "${score.score}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ActionsTable(actions: List<CharacterAction>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            // Başlık satırı
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            ) {
                Text(
                    text = "Eylem",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "İsabet",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(64.dp),
                    textAlign = TextAlign.End,
                )
                Text(
                    text = "Hasar",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(72.dp),
                    textAlign = TextAlign.End,
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            actions.forEachIndexed { index, action ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = action.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = action.attackBonus,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.width(64.dp),
                        textAlign = TextAlign.End,
                    )
                    Text(
                        text = action.damage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(72.dp),
                        textAlign = TextAlign.End,
                    )
                }
                if (index < actions.lastIndex) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}

@Composable
private fun EquipmentRow(item: String, accent: Color) {
    Row(verticalAlignment = Alignment.Top) {
        Text(
            text = "•",
            style = MaterialTheme.typography.bodyMedium,
            color = accent,
            modifier = Modifier.padding(top = 1.dp, end = 10.dp),
        )
        Text(
            text = item,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
private fun StatLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun StatValue(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onBackground,
    )
}

// — Yardımcı fonksiyon —

private fun Int.toSignedString() = if (this >= 0) "+$this" else "$this"
