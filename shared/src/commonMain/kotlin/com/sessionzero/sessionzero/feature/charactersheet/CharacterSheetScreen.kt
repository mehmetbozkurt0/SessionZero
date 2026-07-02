package com.sessionzero.sessionzero.feature.charactersheet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sessionzero.sessionzero.data.character.CharacterRepository
import com.sessionzero.sessionzero.data.dnd5e.DndBackground
import com.sessionzero.sessionzero.data.dnd5e.DndClass
import com.sessionzero.sessionzero.data.dnd5e.DndRace
import com.sessionzero.sessionzero.data.dnd5e.Dnd5eSkill
import com.sessionzero.sessionzero.data.dnd5e.dndSubclasses
import kotlinx.coroutines.delay
import com.sessionzero.sessionzero.ui.theme.LocalSessionZeroAccent
import com.sessionzero.sessionzero.ui.theme.accentColor

// — Shared "desktop paper" look —

private val SheetCardShape = CutCornerShape(4.dp)

@Composable
private fun sheetBorder(alpha: Float = 0.3f) =
    BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = alpha))

// — Screen entry point —

@Composable
fun CharacterSheetScreen(
    dndClass: DndClass?,
    characterId: Long?,
    characterRepository: CharacterRepository,
    isBlankSheet: Boolean = false,
    viewModel: CharacterSheetViewModel = viewModel(
        key = "sheet_${characterId}_${dndClass?.id}_$isBlankSheet"
    ) { CharacterSheetViewModel(dndClass, characterId, isBlankSheet, characterRepository) },
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

    val accent = state.dndClass?.category?.accentColor ?: Color(0xFF1A1A2E)

    CompositionLocalProvider(LocalSessionZeroAccent provides accent) {
        if (state.isLoading || state.dndClass == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            CharacterSheetContent(
                state = state,
                showSavedMessage = showSavedMessage,
                onIntent = viewModel::onIntent,
            )
        }
    }
}

// — Main content —

@Composable
private fun CharacterSheetContent(
    state: CharacterSheetContract.State,
    showSavedMessage: Boolean,
    onIntent: (CharacterSheetContract.Intent) -> Unit,
) {
    val accent = LocalSessionZeroAccent.current
    val dndClass = state.dndClass!! // guarded by caller
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showClassPicker by remember { mutableStateOf(false) }
    var showRacePicker by remember { mutableStateOf(false) }
    var showSubclassPicker by remember { mutableStateOf(false) }
    var showBackgroundPicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
    ) {
        Spacer(Modifier.height(48.dp))

        Row(verticalAlignment = Alignment.Top) {
            ClassHeader(
                dndClass = dndClass,
                accent = accent,
                isEditing = state.isEditing,
                onClassClick = { showClassPicker = true },
                modifier = Modifier.weight(1f),
            )
            if (state.isEditing) {
                IconButton(onClick = { showDeleteConfirm = true }) {
                    Text(text = "🗑️", style = MaterialTheme.typography.titleLarge)
                }
            }
            EditToggleButton(
                isEditing = state.isEditing,
                accent = accent,
                onToggleEdit = { onIntent(CharacterSheetContract.Intent.ToggleEditMode) },
                onSave = { onIntent(CharacterSheetContract.Intent.SaveChanges) },
            )
        }

        if (showDeleteConfirm) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = false },
                title = { Text("Delete Character") },
                text = { Text("Are you sure you want to permanently delete this character? This action cannot be undone.") },
                confirmButton = {
                    TextButton(onClick = {
                        showDeleteConfirm = false
                        onIntent(CharacterSheetContract.Intent.DeleteCharacter)
                    }) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirm = false }) {
                        Text("Cancel")
                    }
                },
            )
        }

        if (showClassPicker) {
            SelectionDialog(
                title = "Select Class",
                options = DndClass.entries,
                optionLabel = { it.displayName },
                isSelected = { it == state.dndClass },
                accent = accent,
                onOptionSelected = { onIntent(CharacterSheetContract.Intent.UpdateClass(it)) },
                onDismissRequest = { showClassPicker = false },
            )
        }

        if (showRacePicker) {
            SelectionDialog(
                title = "Select Race",
                options = DndRace.entries,
                optionLabel = { it.displayName },
                isSelected = { it.displayName == state.race },
                accent = accent,
                onOptionSelected = { onIntent(CharacterSheetContract.Intent.UpdateRace(it.displayName)) },
                onDismissRequest = { showRacePicker = false },
            )
        }

        if (showSubclassPicker) {
            SelectionDialog(
                title = "Select Subclass",
                options = dndSubclasses[state.dndClass] ?: emptyList(),
                optionLabel = { it },
                isSelected = { it == state.subclassSuggestion },
                accent = accent,
                onOptionSelected = { onIntent(CharacterSheetContract.Intent.UpdateSubclass(it)) },
                onDismissRequest = { showSubclassPicker = false },
                emptyMessage = "Select a class first.",
            )
        }

        if (showBackgroundPicker) {
            SelectionDialog(
                title = "Select Background",
                options = DndBackground.entries,
                optionLabel = { it.displayName },
                isSelected = { it.displayName == state.background },
                accent = accent,
                onOptionSelected = { onIntent(CharacterSheetContract.Intent.UpdateBackground(it.displayName)) },
                onDismissRequest = { showBackgroundPicker = false },
            )
        }

        AiTagRow(
            race = state.race,
            subclassSuggestion = state.subclassSuggestion,
            background = state.background,
            accent = accent,
            isEditing = state.isEditing,
            onRaceClick = { showRacePicker = true },
            onSubclassClick = { showSubclassPicker = true },
            onBackgroundClick = { showBackgroundPicker = true },
        )

        Spacer(Modifier.height(24.dp))

        if (state.isEditing) {
            OutlinedTextField(
                value = state.characterName,
                onValueChange = { onIntent(CharacterSheetContract.Intent.UpdateName(it)) },
                label = { Text("Character Name") },
                placeholder = { Text("Name your hero...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(2.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = accent,
                    focusedLabelColor = accent,
                    cursorColor = accent,
                ),
            )
        } else {
            Text(
                text = state.characterName.ifBlank { "Unnamed Hero" },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(Modifier.height(16.dp))
        DashboardRow(state = state, accent = accent)

        OrnateDivider(accent = accent)
        SectionHeader(title = "Ability Scores", accent = accent)
        Spacer(Modifier.height(12.dp))
        AbilityScoresGrid(
            scores = state.abilityScores,
            accent = accent,
            isEditing = state.isEditing,
            onScoreChange = { statName, newValue ->
                onIntent(CharacterSheetContract.Intent.UpdateStat(statName, newValue))
            },
        )

        OrnateDivider(accent = accent)
        SectionHeader(title = "Skills", accent = accent)
        Spacer(Modifier.height(12.dp))
        SkillsPanel(
            state = state,
            accent = accent,
            onToggleSkill = { skill -> onIntent(CharacterSheetContract.Intent.ToggleSkillProficiency(skill)) },
        )

        OrnateDivider(accent = accent)
        SectionHeader(title = "Hit Points", accent = accent)
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StatLabel("Level")
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
            StatLabel("Hit Dice")
            StatValue(state.hitDiceLabel)
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
            StatLabel("Base HP (Level 1)")
            if (state.isEditing) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    HpEditField(
                        value = state.baseHp,
                        accent = accent,
                        onValueChange = { onIntent(CharacterSheetContract.Intent.UpdateHp(it)) },
                    )
                    Text(
                        text = " + CON mod.",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            } else {
                StatValue("${state.baseHp} + CON mod.")
            }
        }

        Spacer(Modifier.height(20.dp))
        DeathSavesRow(
            successes = state.deathSaveSuccesses,
            failures = state.deathSaveFailures,
            isEditing = state.isEditing,
            onToggle = { isSuccess, index ->
                onIntent(CharacterSheetContract.Intent.ToggleDeathSave(isSuccess, index))
            },
        )

        OrnateDivider(accent = accent)
        SectionHeader(title = "Attacks & Spells", accent = accent)
        Spacer(Modifier.height(12.dp))
        ActionsTable(actions = state.actions)

        OrnateDivider(accent = accent)
        SectionHeader(title = "Starting Equipment", accent = accent)
        Spacer(Modifier.height(12.dp))
        dndClass.startingEquipment.forEach { item ->
            EquipmentRow(item = item, accent = accent)
            Spacer(Modifier.height(6.dp))
        }

        OrnateDivider(accent = accent)
        SectionHeader(title = "Backstory", accent = accent)
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
            shape = RoundedCornerShape(2.dp),
            colors = ButtonDefaults.buttonColors(containerColor = accent),
        ) {
            Text(
                text = "Save Character",
                style = MaterialTheme.typography.labelLarge,
            )
        }

        AnimatedVisibility(
            visible = showSavedMessage,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Text(
                text = "✓ Character saved successfully",
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
            shape = RoundedCornerShape(2.dp),
        ) {
            Text(
                text = "Start Over",
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

// — Section components —

@Composable
private fun ClassHeader(
    dndClass: DndClass,
    accent: Color,
    isEditing: Boolean,
    onClassClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(68.dp)
                .background(accent),
        )
        Spacer(Modifier.width(16.dp))
        Column {
            Text(
                text = "Character Sheet",
                style = MaterialTheme.typography.labelLarge,
                color = accent,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = if (isEditing) Modifier.clickable(onClick = onClassClick) else Modifier,
            ) {
                Text(
                    text = dndClass.displayName,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                if (isEditing) {
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "✎",
                        style = MaterialTheme.typography.bodyMedium,
                        color = accent,
                    )
                }
            }
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
private fun <T> SelectionDialog(
    title: String,
    options: List<T>,
    optionLabel: (T) -> String,
    isSelected: (T) -> Boolean,
    accent: Color,
    onOptionSelected: (T) -> Unit,
    onDismissRequest: () -> Unit,
    emptyMessage: String = "No options available.",
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(title) },
        text = {
            if (options.isEmpty()) {
                Text(
                    text = emptyMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    options.forEach { option ->
                        val selected = isSelected(option)
                        Text(
                            text = optionLabel(option),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            color = if (selected) accent else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onOptionSelected(option)
                                    onDismissRequest()
                                }
                                .padding(vertical = 12.dp),
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Close")
            }
        },
    )
}

@Composable
private fun SectionHeader(title: String, accent: Color) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = accent,
    )
}

// A hand-drawn-feeling separator between major sections: "─── ♦ ───"
@Composable
private fun OrnateDivider(accent: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = accent.copy(alpha = 0.35f), thickness = 1.dp)
        Text(
            text = "♦",
            style = MaterialTheme.typography.bodySmall,
            color = accent,
            modifier = Modifier.padding(horizontal = 10.dp),
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = accent.copy(alpha = 0.35f), thickness = 1.dp)
    }
}

@Composable
private fun DashboardRow(state: CharacterSheetContract.State, accent: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        DashboardStatCard(label = "AC", value = "${state.combatStats.armorClass}", accent = accent, modifier = Modifier.weight(1f))
        DashboardStatCard(label = "Init", value = state.combatStats.initiative.toSignedString(), accent = accent, modifier = Modifier.weight(1f))
        DashboardStatCard(label = "Speed", value = "${state.combatStats.speedFt}ft", accent = accent, modifier = Modifier.weight(1f))
        DashboardStatCard(label = "Prof.", value = state.proficiencyBonus.toSignedString(), accent = accent, modifier = Modifier.weight(1f))
        DashboardStatCard(label = "Passive", value = "${state.passivePerception}", accent = accent, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun DashboardStatCard(label: String, value: String, accent: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = SheetCardShape,
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = sheetBorder(),
        tonalElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = accent,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun AbilityScoresGrid(
    scores: List<AbilityScore>,
    accent: Color,
    isEditing: Boolean,
    onScoreChange: (String, Int) -> Unit,
) {
    val rows = scores.chunked(3)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        rows.forEach { rowScores ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                rowScores.forEach { score ->
                    AbilityScoreCard(
                        score = score,
                        accent = accent,
                        isEditing = isEditing,
                        onScoreChange = { newValue -> onScoreChange(score.abbreviation, newValue) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun AbilityScoreCard(
    score: AbilityScore,
    accent: Color,
    isEditing: Boolean,
    onScoreChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = SheetCardShape,
        color = MaterialTheme.colorScheme.surface,
        border = sheetBorder(),
        tonalElevation = 0.dp,
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
            if (isEditing) {
                AbilityScoreEditField(value = score.score, accent = accent, onValueChange = onScoreChange)
            } else {
                Text(
                    text = "${score.score}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun AbilityScoreEditField(value: Int, accent: Color, onValueChange: (Int) -> Unit) {
    var text by remember { mutableStateOf(value.toString()) }
    BasicTextField(
        value = text,
        onValueChange = { raw ->
            val filtered = raw.filter { it.isDigit() }
            text = filtered
            filtered.toIntOrNull()?.let(onValueChange)
        },
        singleLine = true,
        textStyle = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        cursorBrush = SolidColor(accent),
        modifier = Modifier.width(28.dp),
    )
}

@Composable
private fun HpEditField(value: Int, accent: Color, onValueChange: (Int) -> Unit) {
    var text by remember { mutableStateOf(value.toString()) }
    BasicTextField(
        value = text,
        onValueChange = { raw ->
            val filtered = raw.filter { it.isDigit() }
            text = filtered
            filtered.toIntOrNull()?.let(onValueChange)
        },
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        cursorBrush = SolidColor(accent),
        modifier = Modifier.width(40.dp),
    )
}

@Composable
private fun EditToggleButton(
    isEditing: Boolean,
    accent: Color,
    onToggleEdit: () -> Unit,
    onSave: () -> Unit,
) {
    IconButton(onClick = if (isEditing) onSave else onToggleEdit) {
        Text(
            text = if (isEditing) "✓" else "✎",
            style = MaterialTheme.typography.titleLarge,
            color = accent,
        )
    }
}

@Composable
private fun ActionsTable(actions: List<CharacterAction>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = SheetCardShape,
        color = MaterialTheme.colorScheme.surface,
        border = sheetBorder(),
        tonalElevation = 0.dp,
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            // Header row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            ) {
                Text(
                    text = "Action",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "Attack",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(64.dp),
                    textAlign = TextAlign.End,
                )
                Text(
                    text = "Damage",
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

// — Skills —

@Composable
private fun SkillsPanel(
    state: CharacterSheetContract.State,
    accent: Color,
    onToggleSkill: (Dnd5eSkill) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = SheetCardShape,
        color = MaterialTheme.colorScheme.surface,
        border = sheetBorder(),
        tonalElevation = 0.dp,
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
            Dnd5eSkill.entries.forEach { skill ->
                SkillRow(
                    skill = skill,
                    modifierValue = state.skillModifier(skill),
                    isProficient = skill in state.skillProficiencies,
                    isEditing = state.isEditing,
                    accent = accent,
                    onToggle = { onToggleSkill(skill) },
                )
            }
        }
    }
}

@Composable
private fun SkillRow(
    skill: Dnd5eSkill,
    modifierValue: Int,
    isProficient: Boolean,
    isEditing: Boolean,
    accent: Color,
    onToggle: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .then(if (isEditing) Modifier.clickable(onClick = onToggle) else Modifier)
            .padding(vertical = 8.dp),
    ) {
        Text(
            text = if (isProficient) "◈" else "◇",
            style = MaterialTheme.typography.bodyMedium,
            color = if (isProficient) accent else MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            modifier = Modifier.width(18.dp),
        )
        Text(
            text = modifierValue.toSignedString(),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = if (isProficient) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            modifier = Modifier.width(32.dp),
        )
        Text(
            text = skill.displayName,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = skill.ability,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(32.dp),
            textAlign = TextAlign.End,
        )
    }
}

// — Death saves —

@Composable
private fun DeathSavesRow(
    successes: Int,
    failures: Int,
    isEditing: Boolean,
    onToggle: (Boolean, Int) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        DeathSavePips(
            label = "Death Saves — Success",
            count = successes,
            filledColor = Color(0xFF2E6B4A),
            isEditing = isEditing,
            onToggle = { index -> onToggle(true, index) },
        )
        DeathSavePips(
            label = "Death Saves — Failure",
            count = failures,
            filledColor = Color(0xFF9E2A2A),
            isEditing = isEditing,
            onToggle = { index -> onToggle(false, index) },
        )
    }
}

@Composable
private fun DeathSavePips(
    label: String,
    count: Int,
    filledColor: Color,
    isEditing: Boolean,
    onToggle: (Int) -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(3) { index ->
                val filled = index < count
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .then(if (isEditing) Modifier.clickable { onToggle(index) } else Modifier)
                        .background(
                            color = if (filled) filledColor else Color.Transparent,
                            shape = CircleShape,
                        )
                        .border(
                            width = 1.dp,
                            color = if (filled) filledColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                            shape = CircleShape,
                        ),
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AiTagRow(
    race: String,
    subclassSuggestion: String,
    background: String,
    accent: Color,
    isEditing: Boolean,
    onRaceClick: () -> Unit,
    onSubclassClick: () -> Unit,
    onBackgroundClick: () -> Unit,
) {
    data class Tag(val label: String, val value: String, val onClick: () -> Unit)

    val tags = buildList {
        if (isEditing || race.isNotBlank()) {
            add(Tag("Race", race.ifBlank { "Select Race" }, if (isEditing) onRaceClick else ({})))
        }
        if (isEditing || subclassSuggestion.isNotBlank()) {
            add(Tag("Subclass", subclassSuggestion.ifBlank { "Select Subclass" }, if (isEditing) onSubclassClick else ({})))
        }
        if (isEditing || background.isNotBlank()) {
            add(Tag("Background", background.ifBlank { "Select Background" }, if (isEditing) onBackgroundClick else ({})))
        }
    }
    if (tags.isEmpty()) return

    Spacer(Modifier.height(12.dp))

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        tags.forEach { tag ->
            SuggestionChip(
                onClick = tag.onClick,
                label = {
                    Text(
                        text = "${tag.label}: ${tag.value}",
                        style = MaterialTheme.typography.labelSmall,
                    )
                },
                shape = RoundedCornerShape(2.dp),
                border = SuggestionChipDefaults.suggestionChipBorder(
                    enabled = true,
                    borderColor = accent.copy(alpha = 0.35f),
                ),
                colors = SuggestionChipDefaults.suggestionChipColors(
                    labelColor = accent,
                    containerColor = accent.copy(alpha = 0.07f),
                ),
            )
        }
    }
}

// — Helper —

private fun Int.toSignedString() = if (this >= 0) "+$this" else "$this"
