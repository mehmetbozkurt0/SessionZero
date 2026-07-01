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
    val attackBonus: String,
    val damage: String,
)

object CharacterSheetContract {

    data class State(
        val dndClass: DndClass? = null,
        val isLoading: Boolean = true,
        val characterName: String = "",
        val level: Int = 1,
        val baseHp: Int = 0,
        val combatStats: CombatStats = CombatStats(10, 0, 30),
        val abilityScores: List<AbilityScore> = emptyList(),
        val actions: List<CharacterAction> = emptyList(),
        val backstory: String = "",
        val race: String = "",
        val subclassSuggestion: String = "",
        val background: String = "",
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
