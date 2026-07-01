package com.sessionzero.sessionzero.data.dnd5e

val dndSubclasses: Map<DndClass, List<String>> = mapOf(
    DndClass.ARTIFICER to listOf(
        "Armorer", "Alchemist", "Artillerist", "Battle Smith",
    ),
    DndClass.BARBARIAN to listOf(
        "Berserker", "Totem Warrior", "Ancestral Guardian", "Storm Herald",
        "Zealot", "Beast", "Wild Soul", "Battlerager",
    ),
    DndClass.BARD to listOf(
        "Lore", "Valor", "Creation", "Glamor", "Swords", "Whispers", "Eloquence", "Spirits",
    ),
    DndClass.CLERIC to listOf(
        "Knowledge", "Life", "Light", "Nature", "Tempest", "Trickery", "War", "Death",
        "Twilight", "Order", "Forge", "Grave", "Peace", "Arcane",
    ),
    DndClass.DRUID to listOf(
        "Land", "Moon", "Dreams", "Shepherd", "Spores", "Stars", "Wildfire",
    ),
    DndClass.FIGHTER to listOf(
        "Champion", "Battle Master", "Eldritch Knight", "Arcane Archer", "Cavalier",
        "Samurai", "Psi Warrior", "Rune Knight", "Echo Fighter", "Purple Dragon Knight",
    ),
    DndClass.MONK to listOf(
        "Open Hand", "Shadow", "Four Elements", "Mercy", "Astral Self",
        "Drunken Master", "Kensei", "Sun Soul", "Long Death", "Ascendant Dragon",
    ),
    DndClass.PALADIN to listOf(
        "Devotion", "Ancients", "Vengeance", "Oathbreaker", "Conquest",
        "Redemption", "Glory", "Watchers", "Crown",
    ),
    DndClass.RANGER to listOf(
        "Fey Wanderer", "Swarmkeeper", "Gloom Stalker", "Horizon Walker",
        "Monster Slayer", "Hunter", "Beast Master", "Drakewarden",
    ),
    DndClass.ROGUE to listOf(
        "Thief", "Assassin", "Arcane Trickster", "Inquisitive", "Mastermind",
        "Scout", "Swashbuckler", "Phantom", "Soulknife",
    ),
    DndClass.SORCERER to listOf(
        "Aberrant Mind", "Clockwork Soul", "Divine Soul", "Shadow Magic",
        "Storm Sorcery", "Draconic Bloodline", "Wild Magic",
    ),
    DndClass.WARLOCK to listOf(
        "Archfey", "Fiend", "Great Old One", "Celestial", "Undying",
        "Hexblade", "Fathomless", "Genie", "Undead",
    ),
    DndClass.WIZARD to listOf(
        "Abjuration", "Conjuration", "Divination", "Enchantment", "Evocation",
        "Illusion", "Necromancy", "Transmutation", "Graviturgy", "Chronurgy",
        "War Magic", "Bladesinging", "Order of Scribes",
    ),
)
