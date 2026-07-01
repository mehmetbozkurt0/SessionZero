package com.sessionzero.sessionzero.data.dnd5e

data class DecisionOption(
    val id: String,
    val label: String,
    val nextQuestion: DecisionQuestion? = null,
    val result: DndClass? = null,
)

data class DecisionQuestion(
    val id: String,
    val text: String,
    val stepLabel: String,
    val options: List<DecisionOption>,
)

object DecisionTreeData {

    val rootQuestion = DecisionQuestion(
        id = "q1",
        text = "What's your core approach to adventuring?",
        stepLabel = "Approach",
        options = listOf(
            DecisionOption(
                id = "physical",
                label = "Physical Might and Weapons",
                nextQuestion = DecisionQuestion(
                    id = "q2_physical",
                    text = "What do you rely on in a fight?",
                    stepLabel = "Class",
                    options = listOf(
                        DecisionOption(
                            id = "barbarian",
                            label = "Rage and raw power",
                            result = DndClass.BARBARIAN,
                        ),
                        DecisionOption(
                            id = "fighter",
                            label = "Weapon and tactical mastery",
                            result = DndClass.FIGHTER,
                        ),
                        DecisionOption(
                            id = "monk",
                            label = "Bare hands and mystic discipline",
                            result = DndClass.MONK,
                        ),
                    ),
                ),
            ),
            DecisionOption(
                id = "stealth",
                label = "Stealth and Precision",
                nextQuestion = DecisionQuestion(
                    id = "q2_stealth",
                    text = "What's your area of expertise?",
                    stepLabel = "Class",
                    options = listOf(
                        DecisionOption(
                            id = "rogue",
                            label = "Shadows and assassination",
                            result = DndClass.ROGUE,
                        ),
                        DecisionOption(
                            id = "ranger",
                            label = "Archery and wilderness hunting",
                            result = DndClass.RANGER,
                        ),
                        DecisionOption(
                            id = "artificer",
                            label = "Mechanical inventions and tools",
                            result = DndClass.ARTIFICER,
                        ),
                    ),
                ),
            ),
            DecisionOption(
                id = "divine",
                label = "Divinity and Nature",
                nextQuestion = DecisionQuestion(
                    id = "q2_divine",
                    text = "Where does your power come from?",
                    stepLabel = "Class",
                    options = listOf(
                        DecisionOption(
                            id = "cleric",
                            label = "Devotion to the gods and healing",
                            result = DndClass.CLERIC,
                        ),
                        DecisionOption(
                            id = "paladin",
                            label = "Sacred oaths and heavy armor",
                            result = DndClass.PALADIN,
                        ),
                        DecisionOption(
                            id = "druid",
                            label = "Nature's balance and shapeshifting",
                            result = DndClass.DRUID,
                        ),
                    ),
                ),
            ),
            DecisionOption(
                id = "magic",
                label = "Arcane Magic and Lore",
                nextQuestion = DecisionQuestion(
                    id = "q2_magic",
                    text = "Where does your magic power lie?",
                    stepLabel = "Class",
                    options = listOf(
                        DecisionOption(
                            id = "wizard",
                            label = "Magic learned from studying books",
                            result = DndClass.WIZARD,
                        ),
                        DecisionOption(
                            id = "sorcerer",
                            label = "Innate power from your bloodline",
                            result = DndClass.SORCERER,
                        ),
                        DecisionOption(
                            id = "warlock",
                            label = "A pact made with an otherworldly being",
                            result = DndClass.WARLOCK,
                        ),
                        DecisionOption(
                            id = "bard",
                            label = "The magic of music and art",
                            result = DndClass.BARD,
                        ),
                    ),
                ),
            ),
        ),
    )
}
