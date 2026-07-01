package com.sessionzero.sessionzero.feature.charactersheet

import com.sessionzero.sessionzero.data.dnd5e.DndClass
import com.sessionzero.sessionzero.data.dnd5e.Dnd5eSkill
import kotlin.math.ceil

data class CombatStats(
    val armorClass: Int,
    val initiative: Int,
    val speedFt: Int,
)

fun abilityModifier(score: Int): Int = (score - 10).floorDiv(2)

fun proficiencyBonusForLevel(level: Int): Int = ceil((level / 4.0) + 1).toInt()

data class AbilityScore(
    val abbreviation: String,
    val score: Int,
) {
    val modifier: Int get() = abilityModifier(score)
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
        val isEditing: Boolean = false,
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
        val skillProficiencies: Set<Dnd5eSkill> = emptySet(),
        val deathSaveSuccesses: Int = 0,
        val deathSaveFailures: Int = 0,
    ) {
        val proficiencyBonus: Int get() = proficiencyBonusForLevel(level)

        val hitDiceLabel: String get() = dndClass?.let { "1d${it.hitDie}" } ?: "—"

        fun abilityModifierFor(abbreviation: String): Int =
            abilityScores.find { it.abbreviation == abbreviation }?.modifier ?: 0

        val passivePerception: Int
            get() {
                val wisModifier = abilityModifierFor("WIS")
                val bonus = if (Dnd5eSkill.PERCEPTION in skillProficiencies) proficiencyBonus else 0
                return 10 + wisModifier + bonus
            }

        fun skillModifier(skill: Dnd5eSkill): Int {
            val base = abilityModifierFor(skill.ability)
            return if (skill in skillProficiencies) base + proficiencyBonus else base
        }
    }

    sealed interface Intent {
        data class UpdateName(val name: String) : Intent
        data object ToggleEditMode : Intent
        data class UpdateStat(val statName: String, val newValue: Int) : Intent
        data class UpdateHp(val newHp: Int) : Intent
        data class UpdateClass(val newClass: DndClass) : Intent
        data class UpdateRace(val newRace: String) : Intent
        data class UpdateSubclass(val newValue: String) : Intent
        data class UpdateBackground(val newValue: String) : Intent
        data class ToggleSkillProficiency(val skill: Dnd5eSkill) : Intent
        data class ToggleDeathSave(val isSuccess: Boolean, val index: Int) : Intent
        data object SaveChanges : Intent
        data object DeleteCharacter : Intent
        data object SaveCharacter : Intent
        data object StartOver : Intent
    }

    sealed interface Effect {
        data object NavigateToSystemSelection : Effect
        data object ShowSaveSuccess : Effect
    }
}
