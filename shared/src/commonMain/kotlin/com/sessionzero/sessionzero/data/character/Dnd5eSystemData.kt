package com.sessionzero.sessionzero.data.character

import kotlinx.serialization.Serializable

@Serializable
data class Dnd5eSystemData(
    val className: String,
    val displayName: String,
    val primaryStat: String,
    val hp: Int,
    val armorClass: Int,
    val initiative: Int,
    val speedFt: Int,
    val abilityScores: Map<String, Int>,
    val actions: List<ActionData>,
    val startingEquipment: List<String>,
    val backstory: String,
    val race: String = "",
    val subclassSuggestion: String = "",
    val background: String = "",
    val level: Int = 1,
    val skillProficiencies: Set<String> = emptySet(),
    val deathSaveSuccesses: Int = 0,
    val deathSaveFailures: Int = 0,
)

@Serializable
data class ActionData(
    val name: String,
    val attackBonus: String,
    val damage: String,
)
