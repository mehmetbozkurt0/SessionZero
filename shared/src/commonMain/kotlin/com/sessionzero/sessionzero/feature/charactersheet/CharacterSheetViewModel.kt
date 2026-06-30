package com.sessionzero.sessionzero.feature.charactersheet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sessionzero.sessionzero.data.dnd5e.DndClass
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CharacterSheetViewModel(dndClass: DndClass) : ViewModel() {

    private val _state = MutableStateFlow(
        CharacterSheetContract.State(
            dndClass = dndClass,
            combatStats = mockCombatStats(dndClass),
            abilityScores = mockAbilityScores(dndClass),
            actions = mockActions(dndClass),
        )
    )
    val state: StateFlow<CharacterSheetContract.State> = _state.asStateFlow()

    private val _effect = Channel<CharacterSheetContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: CharacterSheetContract.Intent) {
        when (intent) {
            is CharacterSheetContract.Intent.UpdateName ->
                _state.value = _state.value.copy(characterName = intent.name)

            CharacterSheetContract.Intent.StartOver -> viewModelScope.launch {
                _effect.send(CharacterSheetContract.Effect.NavigateToSystemSelection)
            }
        }
    }
}

// — Mock data yardımcıları —
// İlerideki hikaye temelli akışta bu fonksiyonların yerini
// AI çıktısından türetilen gerçek veriler alacak.

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

// Standart dizi (15,14,13,12,10,8) her sınıfın önceliğine göre dağıtıldı.
private fun mockAbilityScores(dndClass: DndClass): List<AbilityScore> {
    fun scores(str: Int, dex: Int, con: Int, int: Int, wis: Int, cha: Int) = listOf(
        AbilityScore("STR", str),
        AbilityScore("DEX", dex),
        AbilityScore("CON", con),
        AbilityScore("INT", int),
        AbilityScore("WIS", wis),
        AbilityScore("CHA", cha),
    )
    return when (dndClass) {
        DndClass.BARBARIAN -> scores(str=15, dex=13, con=14, int=8,  wis=12, cha=10)
        DndClass.FIGHTER   -> scores(str=15, dex=13, con=14, int=8,  wis=12, cha=10)
        DndClass.MONK      -> scores(str=12, dex=15, con=13, int=8,  wis=14, cha=10)
        DndClass.ROGUE     -> scores(str=10, dex=15, con=13, int=12, wis=8,  cha=14)
        DndClass.RANGER    -> scores(str=12, dex=15, con=13, int=8,  wis=14, cha=10)
        DndClass.ARTIFICER -> scores(str=10, dex=14, con=13, int=15, wis=12, cha=8)
        DndClass.CLERIC    -> scores(str=13, dex=10, con=12, int=8,  wis=15, cha=14)
        DndClass.PALADIN   -> scores(str=15, dex=10, con=13, int=8,  wis=12, cha=14)
        DndClass.DRUID     -> scores(str=10, dex=12, con=13, int=8,  wis=15, cha=14)
        DndClass.WIZARD    -> scores(str=8,  dex=12, con=13, int=15, wis=14, cha=10)
        DndClass.SORCERER  -> scores(str=8,  dex=13, con=14, int=10, wis=12, cha=15)
        DndClass.WARLOCK   -> scores(str=10, dex=13, con=14, int=8,  wis=12, cha=15)
        DndClass.BARD      -> scores(str=8,  dex=14, con=13, int=12, wis=10, cha=15)
    }
}

private fun mockActions(dndClass: DndClass): List<CharacterAction> = when (dndClass) {
    DndClass.BARBARIAN -> listOf(
        CharacterAction("Büyük Balta",        "+5",    "1d12+3"),
        CharacterAction("El Çekici (bonus)",  "+5",    "1d6+3"),
    )
    DndClass.FIGHTER -> listOf(
        CharacterAction("Uzun Kılıç",         "+5",    "1d8+3"),
        CharacterAction("Kalkan Darbesi",     "+5",    "1d4+3"),
    )
    DndClass.MONK -> listOf(
        CharacterAction("Kısa Kılıç",         "+4",    "1d6+2"),
        CharacterAction("Yumruk (bonus)",     "+4",    "1d4+2"),
    )
    DndClass.ROGUE -> listOf(
        CharacterAction("Kısa Kılıç",         "+4",    "1d6+2 (+2d6 Hile)"),
        CharacterAction("El Yayı",            "+4",    "1d6+2"),
    )
    DndClass.RANGER -> listOf(
        CharacterAction("Uzun Yay",           "+4",    "1d8+2"),
        CharacterAction("Kısa Kılıç",         "+4",    "1d6+2"),
    )
    DndClass.ARTIFICER -> listOf(
        CharacterAction("El Çekici",          "+3",    "1d6+1"),
        CharacterAction("Arcane Firearm",     "+5",    "1d10"),
    )
    DndClass.CLERIC -> listOf(
        CharacterAction("Savaş Çekici",       "+4",    "1d8+2"),
        CharacterAction("Sacred Flame",       "KUR 13","1d8 Yangın"),
    )
    DndClass.PALADIN -> listOf(
        CharacterAction("Uzun Kılıç",         "+5",    "1d8+3"),
        CharacterAction("İlahi Yıkım",        "+5",    "1d8+3 +2d8"),
    )
    DndClass.DRUID -> listOf(
        CharacterAction("Şimşek Çarpması",    "+5",    "1d8 Elektrik"),
        CharacterAction("Eğri Hançer",        "+2",    "1d4"),
    )
    DndClass.WIZARD -> listOf(
        CharacterAction("Sihirli Tüfek",      "+5",    "1d10 Güç"),
        CharacterAction("Yanan Eller",        "KUR 13","3d6 Ateş"),
    )
    DndClass.SORCERER -> listOf(
        CharacterAction("Alev Patlaması",     "+5",    "1d10 Ateş"),
        CharacterAction("Büyücü Kalkanı",     "KUR 13","2d8 Şimşek"),
    )
    DndClass.WARLOCK -> listOf(
        CharacterAction("Eldritch Blast",     "+5",    "1d10 Güç"),
        CharacterAction("Hex (1. slot)",      "—",     "+1d6 Lanet"),
    )
    DndClass.BARD -> listOf(
        CharacterAction("Rapiyer",            "+4",    "1d8+2"),
        CharacterAction("Vicious Mockery",    "KUR 13","1d4 Psiyo."),
    )
}
