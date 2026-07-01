package com.sessionzero.sessionzero.data.dnd5e

enum class Dnd5eSkill(
    val displayName: String,
    val ability: String,
) {
    ACROBATICS("Acrobatics", "DEX"),
    ANIMAL_HANDLING("Animal Handling", "WIS"),
    ARCANA("Arcana", "INT"),
    ATHLETICS("Athletics", "STR"),
    DECEPTION("Deception", "CHA"),
    HISTORY("History", "INT"),
    INSIGHT("Insight", "WIS"),
    INTIMIDATION("Intimidation", "CHA"),
    INVESTIGATION("Investigation", "INT"),
    MEDICINE("Medicine", "WIS"),
    NATURE("Nature", "INT"),
    PERCEPTION("Perception", "WIS"),
    PERFORMANCE("Performance", "CHA"),
    PERSUASION("Persuasion", "CHA"),
    RELIGION("Religion", "INT"),
    SLEIGHT_OF_HAND("Sleight of Hand", "DEX"),
    STEALTH("Stealth", "DEX"),
    SURVIVAL("Survival", "WIS");

    companion object {
        fun fromId(id: String): Dnd5eSkill? = entries.find { it.name == id }
    }
}
