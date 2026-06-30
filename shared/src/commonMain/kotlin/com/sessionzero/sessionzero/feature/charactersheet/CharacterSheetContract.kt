package com.sessionzero.sessionzero.feature.charactersheet

import com.sessionzero.sessionzero.data.dnd5e.DndClass

data class CombatStats(
    val armorClass: Int,
    val initiative: Int,
    val speedFt: Int,
)

data class AbilityScore(
    val abbreviation: String,
    val score: Int,
) {
    val modifier: Int get() = (score - 10).floorDiv(2)
}

data class CharacterAction(
    val name: String,
    val attackBonus: String, // "+5" veya "KUR 13"
    val damage: String,      // "1d8+3"
)

object CharacterSheetContract {

    data class State(
        val dndClass: DndClass,
        val characterName: String = "",
        val level: Int = 1,
        val baseHp: Int = dndClass.hitDie,
        val combatStats: CombatStats,
        val abilityScores: List<AbilityScore>, // STR, DEX, CON, INT, WIS, CHA sırası
        val actions: List<CharacterAction>,
        val backstory: String = "...",
    )

    sealed interface Intent {
        data class UpdateName(val name: String) : Intent
        data object SaveCharacter : Intent
        data object StartOver : Intent
    }

    sealed interface Effect {
        data object NavigateToSystemSelection : Effect
        data object ShowSaveSuccess : Effect
    }
}
