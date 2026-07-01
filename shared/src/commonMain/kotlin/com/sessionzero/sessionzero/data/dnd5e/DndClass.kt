package com.sessionzero.sessionzero.data.dnd5e

// Extension point for future AI-driven injection:
// DndClass.fromId() is the resolution layer — swap it to load from
// a remote/dynamic source when the story-based flow is implemented.
enum class DndClass(
    val id: String,
    val displayName: String,
    val primaryStat: String,
    val hitDie: Int,
    val flavor: String,
    val category: ClassCategory,
    val startingEquipment: List<String>,
) {
    BARBARIAN(
        id = "barbarian",
        displayName = "Barbarian",
        primaryStat = "Strength (STR)",
        hitDie = 12,
        flavor = "Fights through rage — unarmored, relentless, devastating.",
        category = ClassCategory.PHYSICAL,
        startingEquipment = listOf(
            "Greataxe",
            "Two handaxes",
            "Four javelins",
            "Explorer's pack",
        ),
    ),
    FIGHTER(
        id = "fighter",
        displayName = "Fighter",
        primaryStat = "Strength or Dexterity",
        hitDie = 10,
        flavor = "Adapts to any kind of fight through weapon and tactical mastery.",
        category = ClassCategory.PHYSICAL,
        startingEquipment = listOf(
            "Chain mail",
            "Longsword and steel shield",
            "Five javelins",
        ),
    ),
    MONK(
        id = "monk",
        displayName = "Monk",
        primaryStat = "Dexterity and Wisdom",
        hitDie = 8,
        flavor = "Unites body and mind through unarmed strikes and mystic discipline.",
        category = ClassCategory.PHYSICAL,
        startingEquipment = listOf(
            "Shortsword",
            "Ten javelins",
            "Explorer's pack",
        ),
    ),
    ROGUE(
        id = "rogue",
        displayName = "Rogue",
        primaryStat = "Dexterity (DEX)",
        hitDie = 8,
        flavor = "Moves through shadows and lands a critical blow with a single strike.",
        category = ClassCategory.STEALTH,
        startingEquipment = listOf(
            "Leather armor",
            "Two shortswords",
            "Thieves' tools",
        ),
    ),
    RANGER(
        id = "ranger",
        displayName = "Ranger",
        primaryStat = "Dexterity and Wisdom",
        hitDie = 10,
        flavor = "Master of the wild; relies on the bow, traps, and an animal companion.",
        category = ClassCategory.STEALTH,
        startingEquipment = listOf(
            "Leather armor",
            "Longbow and twenty arrows",
            "Two shortswords",
        ),
    ),
    ARTIFICER(
        id = "artificer",
        displayName = "Artificer",
        primaryStat = "Intelligence (INT)",
        hitDie = 8,
        flavor = "Empowers the party with mechanical inventions and magical tools.",
        category = ClassCategory.STEALTH,
        startingEquipment = listOf(
            "Leather armor",
            "Two handaxes",
            "Tinker's tools",
        ),
    ),
    CLERIC(
        id = "cleric",
        displayName = "Cleric",
        primaryStat = "Wisdom (WIS)",
        hitDie = 8,
        flavor = "Heals with their god's power and fights with the light of the divine.",
        category = ClassCategory.DIVINE,
        startingEquipment = listOf(
            "Chain mail",
            "Steel shield",
            "Warhammer",
            "Holy symbol",
        ),
    ),
    PALADIN(
        id = "paladin",
        displayName = "Paladin",
        primaryStat = "Strength and Charisma",
        hitDie = 10,
        flavor = "A heavily armored divine knight bound by a sacred oath.",
        category = ClassCategory.DIVINE,
        startingEquipment = listOf(
            "Plate armor",
            "Longsword and holy shield",
            "Holy symbol",
        ),
    ),
    DRUID(
        id = "druid",
        displayName = "Druid",
        primaryStat = "Wisdom (WIS)",
        hitDie = 8,
        flavor = "Preserves nature's balance, shapeshifting into animals and elements.",
        category = ClassCategory.DIVINE,
        startingEquipment = listOf(
            "Wooden shield",
            "Leather armor",
            "Druidic focus",
        ),
    ),
    WIZARD(
        id = "wizard",
        displayName = "Wizard",
        primaryStat = "Intelligence (INT)",
        hitDie = 6,
        flavor = "Transforms anything with spells learned through years of study.",
        category = ClassCategory.MAGIC,
        startingEquipment = listOf(
            "Spellbook",
            "Arcane focus",
            "Explorer's pack",
        ),
    ),
    SORCERER(
        id = "sorcerer",
        displayName = "Sorcerer",
        primaryStat = "Charisma (CHA)",
        hitDie = 6,
        flavor = "Wields magic instinctively through innate power from their bloodline.",
        category = ClassCategory.MAGIC,
        startingEquipment = listOf(
            "Light crossbow and twenty bolts",
            "Arcane focus",
            "Explorer's pack",
        ),
    ),
    WARLOCK(
        id = "warlock",
        displayName = "Warlock",
        primaryStat = "Charisma (CHA)",
        hitDie = 8,
        flavor = "Gains power in exchange for a pact made with an otherworldly patron.",
        category = ClassCategory.MAGIC,
        startingEquipment = listOf(
            "Leather armor",
            "Simple weapon",
            "Arcane focus",
        ),
    ),
    BARD(
        id = "bard",
        displayName = "Bard",
        primaryStat = "Charisma (CHA)",
        hitDie = 8,
        flavor = "Fights and enchants the party alike through the magic of music and art.",
        category = ClassCategory.MAGIC,
        startingEquipment = listOf(
            "Leather armor",
            "Rapier",
            "Musical instrument (lute)",
        ),
    );

    companion object {
        fun fromId(id: String): DndClass? = entries.find { it.id == id }
    }
}
