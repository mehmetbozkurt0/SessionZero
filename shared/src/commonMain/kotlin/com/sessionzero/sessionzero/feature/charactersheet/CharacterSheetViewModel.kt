package com.sessionzero.sessionzero.feature.charactersheet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sessionzero.sessionzero.data.character.ActionData
import com.sessionzero.sessionzero.data.character.CharacterRepository
import com.sessionzero.sessionzero.data.character.Dnd5eSystemData
import com.sessionzero.sessionzero.data.dnd5e.DndClass
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
    private val characterId: Long?,        // null → create mode
    private val repository: CharacterRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CharacterSheetContract.State())
    val state: StateFlow<CharacterSheetContract.State> = _state.asStateFlow()

    private val _effect = Channel<CharacterSheetContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private val json = Json { ignoreUnknownKeys = true }

    init {
        if (characterId != null) {
            loadCharacter(characterId)
        } else if (dndClass != null) {
            _state.value = CharacterSheetContract.State(
                dndClass = dndClass,
                isLoading = false,
                baseHp = dndClass.hitDie,
                combatStats = mockCombatStats(dndClass),
                abilityScores = mockAbilityScores(dndClass),
                actions = mockActions(dndClass),
            )
        }
    }

    private fun loadCharacter(id: Long) = viewModelScope.launch {
        val record = repository.getCharacterById(id) ?: run {
            _effect.send(CharacterSheetContract.Effect.NavigateToSystemSelection)
            return@launch
        }
        runCatching {
            val data = json.decodeFromString<Dnd5eSystemData>(record.systemData)
            val orderedKeys = listOf("STR", "DEX", "CON", "INT", "WIS", "CHA")
            _state.value = CharacterSheetContract.State(
                dndClass = DndClass.fromId(data.className),
                isLoading = false,
                characterName = record.name,
                level = 1,
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
            )
        }.onFailure {
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun onIntent(intent: CharacterSheetContract.Intent) {
        when (intent) {
            is CharacterSheetContract.Intent.UpdateName ->
                _state.value = _state.value.copy(characterName = intent.name)

            CharacterSheetContract.Intent.SaveCharacter -> saveCharacter()

            CharacterSheetContract.Intent.StartOver -> viewModelScope.launch {
                _effect.send(CharacterSheetContract.Effect.NavigateToSystemSelection)
            }
        }
    }

    private fun saveCharacter() = viewModelScope.launch {
        val s = _state.value
        val cls = s.dndClass ?: return@launch
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
            )
        )
        val name = s.characterName.ifBlank { cls.displayName }
        if (characterId != null) {
            repository.updateCharacter(characterId, name, systemData)
        } else {
            repository.saveCharacter(name, "DND5E", systemData)
        }
        _effect.send(CharacterSheetContract.Effect.ShowSaveSuccess)
    }
}

// — Mock data yardımcıları —

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

private fun mockActions(dndClass: DndClass): List<CharacterAction> = when (dndClass) {
    DndClass.BARBARIAN -> listOf(CharacterAction("Büyük Balta",       "+5",    "1d12+3"), CharacterAction("El Çekici (bonus)", "+5", "1d6+3"))
    DndClass.FIGHTER   -> listOf(CharacterAction("Uzun Kılıç",        "+5",    "1d8+3"),  CharacterAction("Kalkan Darbesi",    "+5", "1d4+3"))
    DndClass.MONK      -> listOf(CharacterAction("Kısa Kılıç",        "+4",    "1d6+2"),  CharacterAction("Yumruk (bonus)",    "+4", "1d4+2"))
    DndClass.ROGUE     -> listOf(CharacterAction("Kısa Kılıç",        "+4",    "1d6+2 (+2d6)"), CharacterAction("El Yayı",   "+4", "1d6+2"))
    DndClass.RANGER    -> listOf(CharacterAction("Uzun Yay",          "+4",    "1d8+2"),  CharacterAction("Kısa Kılıç",       "+4", "1d6+2"))
    DndClass.ARTIFICER -> listOf(CharacterAction("El Çekici",         "+3",    "1d6+1"),  CharacterAction("Arcane Firearm",   "+5", "1d10"))
    DndClass.CLERIC    -> listOf(CharacterAction("Savaş Çekici",      "+4",    "1d8+2"),  CharacterAction("Sacred Flame",   "KUR 13", "1d8"))
    DndClass.PALADIN   -> listOf(CharacterAction("Uzun Kılıç",        "+5",    "1d8+3"),  CharacterAction("İlahi Yıkım",      "+5", "1d8+3+2d8"))
    DndClass.DRUID     -> listOf(CharacterAction("Şimşek Çarpması",   "+5",    "1d8"),     CharacterAction("Eğri Hançer",     "+2", "1d4"))
    DndClass.WIZARD    -> listOf(CharacterAction("Sihirli Tüfek",     "+5",    "1d10"),   CharacterAction("Yanan Eller",    "KUR 13", "3d6"))
    DndClass.SORCERER  -> listOf(CharacterAction("Alev Patlaması",    "+5",    "1d10"),   CharacterAction("Büyücü Kalkanı", "KUR 13", "2d8"))
    DndClass.WARLOCK   -> listOf(CharacterAction("Eldritch Blast",    "+5",    "1d10"),   CharacterAction("Hex (1. slot)",    "—",  "+1d6"))
    DndClass.BARD      -> listOf(CharacterAction("Rapiyer",           "+4",    "1d8+2"),  CharacterAction("Vicious Mockery","KUR 13", "1d4"))
}
