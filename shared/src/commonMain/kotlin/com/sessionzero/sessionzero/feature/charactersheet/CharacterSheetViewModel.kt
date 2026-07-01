package com.sessionzero.sessionzero.feature.charactersheet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sessionzero.sessionzero.data.character.ActionData
import com.sessionzero.sessionzero.data.character.CharacterRepository
import com.sessionzero.sessionzero.data.character.Dnd5eSystemData
import com.sessionzero.sessionzero.data.dnd5e.DndClass
import com.sessionzero.sessionzero.data.dnd5e.DndRace
import com.sessionzero.sessionzero.data.dnd5e.Dnd5eSkill
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class CharacterSheetViewModel(
    private val dndClass: DndClass?,       // null → view mode (load by characterId)
    initialCharacterId: Long?,             // null → create mode
    private val startBlank: Boolean,       // true → open a blank sheet in edit mode
    private val repository: CharacterRepository,
) : ViewModel() {

    // Tracks the persisted row id. Starts as the id passed in, but is populated
    // after the first insert so later saves UPDATE instead of re-inserting.
    private var characterId: Long? = initialCharacterId

    private val _state = MutableStateFlow(CharacterSheetContract.State())
    val state: StateFlow<CharacterSheetContract.State> = _state.asStateFlow()

    private val _effect = Channel<CharacterSheetContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private val json = Json { ignoreUnknownKeys = true }

    init {
        if (initialCharacterId != null) {
            loadCharacter(initialCharacterId)
        } else if (dndClass != null) {
            _state.value = if (startBlank) {
                stateFromSystemData(emptyDnd5eSystemData(dndClass), characterName = "", isEditing = true)
            } else {
                CharacterSheetContract.State(
                    dndClass = dndClass,
                    isLoading = false,
                    isEditing = true,
                    baseHp = dndClass.hitDie,
                    combatStats = mockCombatStats(dndClass),
                    abilityScores = mockAbilityScores(dndClass),
                    actions = mockActions(dndClass),
                    skillProficiencies = mockSkillProficiencies(dndClass),
                )
            }
        }
    }

    private fun loadCharacter(id: Long) = viewModelScope.launch {
        val record = repository.getCharacterById(id) ?: run {
            _effect.send(CharacterSheetContract.Effect.NavigateToSystemSelection)
            return@launch
        }
        runCatching {
            val data = json.decodeFromString<Dnd5eSystemData>(record.systemData)
            _state.value = stateFromSystemData(data, characterName = record.name, isEditing = false)
        }.onFailure {
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun stateFromSystemData(
        data: Dnd5eSystemData,
        characterName: String,
        isEditing: Boolean,
    ): CharacterSheetContract.State {
        val orderedKeys = listOf("STR", "DEX", "CON", "INT", "WIS", "CHA")
        return CharacterSheetContract.State(
            dndClass = DndClass.fromId(data.className),
            isLoading = false,
            isEditing = isEditing,
            characterName = characterName,
            level = data.level,
            baseHp = data.hp,
            combatStats = CombatStats(data.armorClass, data.initiative, data.speedFt),
            abilityScores = orderedKeys.mapNotNull { key ->
                data.abilityScores[key]?.let { AbilityScore(key, it) }
            },
            actions = data.actions.map { CharacterAction(it.name, it.attackBonus, it.damage) },
            backstory = data.backstory,
            race = data.race,
            subclassSuggestion = data.subclassSuggestion,
            background = data.background,
            skillProficiencies = data.skillProficiencies.mapNotNull { Dnd5eSkill.fromId(it) }.toSet(),
            deathSaveSuccesses = data.deathSaveSuccesses,
            deathSaveFailures = data.deathSaveFailures,
        )
    }

    fun onIntent(intent: CharacterSheetContract.Intent) {
        when (intent) {
            is CharacterSheetContract.Intent.UpdateName ->
                _state.value = _state.value.copy(characterName = intent.name)

            CharacterSheetContract.Intent.ToggleEditMode ->
                _state.update { it.copy(isEditing = !it.isEditing) }

            is CharacterSheetContract.Intent.UpdateStat -> _state.update { s ->
                val updatedScores = s.abilityScores.map { ability ->
                    if (ability.abbreviation == intent.statName) {
                        ability.copy(score = intent.newValue)
                    } else {
                        ability
                    }
                }
                val combatStats = if (intent.statName == "DEX") {
                    val dexModifier = abilityModifier(intent.newValue)
                    s.combatStats.copy(initiative = dexModifier, armorClass = 10 + dexModifier)
                } else {
                    s.combatStats
                }
                s.copy(abilityScores = updatedScores, combatStats = combatStats)
            }

            is CharacterSheetContract.Intent.UpdateHp ->
                _state.update { it.copy(baseHp = intent.newHp) }

            is CharacterSheetContract.Intent.UpdateClass ->
                _state.update { it.copy(dndClass = intent.newClass) }

            is CharacterSheetContract.Intent.UpdateRace -> _state.update { s ->
                val speedFt = DndRace.entries.find { it.displayName == intent.newRace }?.speedFt ?: 30
                s.copy(race = intent.newRace, combatStats = s.combatStats.copy(speedFt = speedFt))
            }

            is CharacterSheetContract.Intent.UpdateSubclass ->
                _state.update { it.copy(subclassSuggestion = intent.newValue) }

            is CharacterSheetContract.Intent.UpdateBackground ->
                _state.update { it.copy(background = intent.newValue) }

            is CharacterSheetContract.Intent.ToggleSkillProficiency -> _state.update { s ->
                val updated = if (intent.skill in s.skillProficiencies) {
                    s.skillProficiencies - intent.skill
                } else {
                    s.skillProficiencies + intent.skill
                }
                s.copy(skillProficiencies = updated)
            }

            is CharacterSheetContract.Intent.ToggleDeathSave -> _state.update { s ->
                val current = if (intent.isSuccess) s.deathSaveSuccesses else s.deathSaveFailures
                val updated = if (intent.index < current) intent.index else intent.index + 1
                if (intent.isSuccess) s.copy(deathSaveSuccesses = updated) else s.copy(deathSaveFailures = updated)
            }

            CharacterSheetContract.Intent.SaveChanges -> viewModelScope.launch {
                saveCharacter()
                _state.update { it.copy(isEditing = false) }
            }

            CharacterSheetContract.Intent.SaveCharacter -> viewModelScope.launch { saveCharacter() }

            CharacterSheetContract.Intent.DeleteCharacter -> viewModelScope.launch {
                characterId?.let { repository.deleteCharacter(it) }
                _effect.send(CharacterSheetContract.Effect.NavigateToSystemSelection)
            }

            CharacterSheetContract.Intent.StartOver -> viewModelScope.launch {
                _effect.send(CharacterSheetContract.Effect.NavigateToSystemSelection)
            }
        }
    }

    private suspend fun saveCharacter() {
        val s = _state.value
        val cls = s.dndClass ?: return
        val systemData = json.encodeToString(
            Dnd5eSystemData(
                className = cls.id,
                displayName = cls.displayName,
                primaryStat = cls.primaryStat,
                hp = s.baseHp,
                armorClass = s.combatStats.armorClass,
                initiative = s.combatStats.initiative,
                speedFt = s.combatStats.speedFt,
                abilityScores = s.abilityScores.associate { it.abbreviation to it.score },
                actions = s.actions.map { ActionData(it.name, it.attackBonus, it.damage) },
                startingEquipment = cls.startingEquipment,
                backstory = s.backstory,
                race = s.race,
                subclassSuggestion = s.subclassSuggestion,
                background = s.background,
                level = s.level,
                skillProficiencies = s.skillProficiencies.map { it.name }.toSet(),
                deathSaveSuccesses = s.deathSaveSuccesses,
                deathSaveFailures = s.deathSaveFailures,
            )
        )
        val name = s.characterName.ifBlank { cls.displayName }
        val existingId = characterId
        if (existingId != null) {
            repository.updateCharacter(existingId, name, systemData)
        } else {
            characterId = repository.saveCharacter(name, "DND5E", systemData)
        }
        _effect.send(CharacterSheetContract.Effect.ShowSaveSuccess)
    }
}

// — Blank sheet helper —

private fun emptyDnd5eSystemData(dndClass: DndClass): Dnd5eSystemData = Dnd5eSystemData(
    className = dndClass.id,
    displayName = dndClass.displayName,
    primaryStat = dndClass.primaryStat,
    hp = 0,
    armorClass = 10,
    initiative = 0,
    speedFt = 30,
    abilityScores = listOf("STR", "DEX", "CON", "INT", "WIS", "CHA").associateWith { 10 },
    actions = emptyList(),
    startingEquipment = dndClass.startingEquipment,
    backstory = "",
    race = "",
    subclassSuggestion = "",
    background = "",
)

// — Mock data helpers —

private fun mockCombatStats(dndClass: DndClass): CombatStats = when (dndClass) {
    DndClass.BARBARIAN -> CombatStats(armorClass = 13, initiative = +1, speedFt = 30)
    DndClass.FIGHTER   -> CombatStats(armorClass = 16, initiative = +1, speedFt = 30)
    DndClass.MONK      -> CombatStats(armorClass = 15, initiative = +2, speedFt = 30)
    DndClass.ROGUE     -> CombatStats(armorClass = 14, initiative = +2, speedFt = 30)
    DndClass.RANGER    -> CombatStats(armorClass = 14, initiative = +2, speedFt = 30)
    DndClass.ARTIFICER -> CombatStats(armorClass = 13, initiative = +2, speedFt = 30)
    DndClass.CLERIC    -> CombatStats(armorClass = 16, initiative =  0, speedFt = 30)
    DndClass.PALADIN   -> CombatStats(armorClass = 18, initiative =  0, speedFt = 30)
    DndClass.DRUID     -> CombatStats(armorClass = 13, initiative = +1, speedFt = 30)
    DndClass.WIZARD    -> CombatStats(armorClass = 11, initiative = +1, speedFt = 30)
    DndClass.SORCERER  -> CombatStats(armorClass = 13, initiative = +1, speedFt = 30)
    DndClass.WARLOCK   -> CombatStats(armorClass = 13, initiative = +1, speedFt = 30)
    DndClass.BARD      -> CombatStats(armorClass = 13, initiative = +2, speedFt = 30)
}

private fun mockAbilityScores(dndClass: DndClass): List<AbilityScore> {
    fun scores(str: Int, dex: Int, con: Int, int: Int, wis: Int, cha: Int) = listOf(
        AbilityScore("STR", str), AbilityScore("DEX", dex), AbilityScore("CON", con),
        AbilityScore("INT", int), AbilityScore("WIS", wis), AbilityScore("CHA", cha),
    )
    return when (dndClass) {
        DndClass.BARBARIAN -> scores(15, 13, 14,  8, 12, 10)
        DndClass.FIGHTER   -> scores(15, 13, 14,  8, 12, 10)
        DndClass.MONK      -> scores(12, 15, 13,  8, 14, 10)
        DndClass.ROGUE     -> scores(10, 15, 13, 12,  8, 14)
        DndClass.RANGER    -> scores(12, 15, 13,  8, 14, 10)
        DndClass.ARTIFICER -> scores(10, 14, 13, 15, 12,  8)
        DndClass.CLERIC    -> scores(13, 10, 12,  8, 15, 14)
        DndClass.PALADIN   -> scores(15, 10, 13,  8, 12, 14)
        DndClass.DRUID     -> scores(10, 12, 13,  8, 15, 14)
        DndClass.WIZARD    -> scores( 8, 12, 13, 15, 14, 10)
        DndClass.SORCERER  -> scores( 8, 13, 14, 10, 12, 15)
        DndClass.WARLOCK   -> scores(10, 13, 14,  8, 12, 15)
        DndClass.BARD      -> scores( 8, 14, 13, 12, 10, 15)
    }
}

private fun mockSkillProficiencies(dndClass: DndClass): Set<Dnd5eSkill> = when (dndClass) {
    DndClass.BARBARIAN -> setOf(Dnd5eSkill.ATHLETICS, Dnd5eSkill.INTIMIDATION)
    DndClass.FIGHTER   -> setOf(Dnd5eSkill.ATHLETICS, Dnd5eSkill.PERCEPTION)
    DndClass.MONK      -> setOf(Dnd5eSkill.ACROBATICS, Dnd5eSkill.STEALTH)
    DndClass.ROGUE     -> setOf(Dnd5eSkill.STEALTH, Dnd5eSkill.SLEIGHT_OF_HAND, Dnd5eSkill.PERCEPTION, Dnd5eSkill.ACROBATICS)
    DndClass.RANGER    -> setOf(Dnd5eSkill.SURVIVAL, Dnd5eSkill.NATURE, Dnd5eSkill.PERCEPTION)
    DndClass.ARTIFICER -> setOf(Dnd5eSkill.INVESTIGATION, Dnd5eSkill.ARCANA)
    DndClass.CLERIC    -> setOf(Dnd5eSkill.RELIGION, Dnd5eSkill.INSIGHT)
    DndClass.PALADIN   -> setOf(Dnd5eSkill.RELIGION, Dnd5eSkill.PERSUASION)
    DndClass.DRUID     -> setOf(Dnd5eSkill.NATURE, Dnd5eSkill.ANIMAL_HANDLING)
    DndClass.WIZARD    -> setOf(Dnd5eSkill.ARCANA, Dnd5eSkill.HISTORY, Dnd5eSkill.INVESTIGATION)
    DndClass.SORCERER  -> setOf(Dnd5eSkill.ARCANA, Dnd5eSkill.PERSUASION)
    DndClass.WARLOCK   -> setOf(Dnd5eSkill.DECEPTION, Dnd5eSkill.ARCANA)
    DndClass.BARD      -> setOf(Dnd5eSkill.PERFORMANCE, Dnd5eSkill.PERSUASION, Dnd5eSkill.DECEPTION)
}

private fun mockActions(dndClass: DndClass): List<CharacterAction> = when (dndClass) {
    DndClass.BARBARIAN -> listOf(CharacterAction("Greataxe",        "+5",   "1d12+3"), CharacterAction("Handaxe (bonus)", "+5", "1d6+3"))
    DndClass.FIGHTER   -> listOf(CharacterAction("Longsword",       "+5",   "1d8+3"),  CharacterAction("Shield Bash",     "+5", "1d4+3"))
    DndClass.MONK      -> listOf(CharacterAction("Shortsword",      "+4",   "1d6+2"),  CharacterAction("Unarmed Strike (bonus)", "+4", "1d4+2"))
    DndClass.ROGUE     -> listOf(CharacterAction("Shortsword",      "+4",   "1d6+2 (+2d6)"), CharacterAction("Hand Crossbow", "+4", "1d6+2"))
    DndClass.RANGER    -> listOf(CharacterAction("Longbow",         "+4",   "1d8+2"),  CharacterAction("Shortsword",      "+4", "1d6+2"))
    DndClass.ARTIFICER -> listOf(CharacterAction("Handaxe",         "+3",   "1d6+1"),  CharacterAction("Arcane Firearm",  "+5", "1d10"))
    DndClass.CLERIC    -> listOf(CharacterAction("Warhammer",       "+4",   "1d8+2"),  CharacterAction("Sacred Flame",    "DC 13", "1d8"))
    DndClass.PALADIN   -> listOf(CharacterAction("Longsword",       "+5",   "1d8+3"),  CharacterAction("Divine Smite",    "+5", "1d8+3+2d8"))
    DndClass.DRUID     -> listOf(CharacterAction("Produce Flame",   "+5",   "1d8"),    CharacterAction("Sickle",          "+2", "1d4"))
    DndClass.WIZARD    -> listOf(CharacterAction("Fire Bolt",       "+5",   "1d10"),   CharacterAction("Burning Hands",   "DC 13", "3d6"))
    DndClass.SORCERER  -> listOf(CharacterAction("Fire Bolt",       "+5",   "1d10"),   CharacterAction("Shield",          "DC 13", "2d8"))
    DndClass.WARLOCK   -> listOf(CharacterAction("Eldritch Blast",  "+5",   "1d10"),   CharacterAction("Hex (1st level)", "—",  "+1d6"))
    DndClass.BARD      -> listOf(CharacterAction("Rapier",          "+4",   "1d8+2"),  CharacterAction("Vicious Mockery", "DC 13", "1d4"))
}
