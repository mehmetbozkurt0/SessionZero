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
    val options: List<DecisionOption>,
)

object DecisionTreeData {

    val rootQuestion = DecisionQuestion(
        id = "q1",
        text = "Maceraya atılırken temel yaklaşımın nedir?",
        options = listOf(
            DecisionOption(
                id = "physical",
                label = "Fiziksel Güç ve Silahlar",
                nextQuestion = DecisionQuestion(
                    id = "q2_physical",
                    text = "Savaşırken neye güvenirsin?",
                    options = listOf(
                        DecisionOption(
                            id = "barbarian",
                            label = "Öfke ve kaba kuvvet",
                            result = DndClass.BARBARIAN,
                        ),
                        DecisionOption(
                            id = "fighter",
                            label = "Silah ve taktik ustalığı",
                            result = DndClass.FIGHTER,
                        ),
                        DecisionOption(
                            id = "monk",
                            label = "Çıplak el ve mistik disiplin",
                            result = DndClass.MONK,
                        ),
                    ),
                ),
            ),
            DecisionOption(
                id = "stealth",
                label = "Gizlilik ve Ustalık",
                nextQuestion = DecisionQuestion(
                    id = "q2_stealth",
                    text = "Uzmanlık alanın nedir?",
                    options = listOf(
                        DecisionOption(
                            id = "rogue",
                            label = "Gölgeler ve suikast",
                            result = DndClass.ROGUE,
                        ),
                        DecisionOption(
                            id = "ranger",
                            label = "Okçuluk ve vahşi doğa avcılığı",
                            result = DndClass.RANGER,
                        ),
                        DecisionOption(
                            id = "artificer",
                            label = "Mekanik icatlar ve aletler",
                            result = DndClass.ARTIFICER,
                        ),
                    ),
                ),
            ),
            DecisionOption(
                id = "divine",
                label = "İlahiyat ve Doğa",
                nextQuestion = DecisionQuestion(
                    id = "q2_divine",
                    text = "Gücünü nereden alırsın?",
                    options = listOf(
                        DecisionOption(
                            id = "cleric",
                            label = "Tanrılara adanmışlık ve şifa",
                            result = DndClass.CLERIC,
                        ),
                        DecisionOption(
                            id = "paladin",
                            label = "Kutsal yeminler ve ağır zırh",
                            result = DndClass.PALADIN,
                        ),
                        DecisionOption(
                            id = "druid",
                            label = "Doğanın dengesi ve hayvanlara dönüşme",
                            result = DndClass.DRUID,
                        ),
                    ),
                ),
            ),
            DecisionOption(
                id = "magic",
                label = "Saf Büyü ve İlim",
                nextQuestion = DecisionQuestion(
                    id = "q2_magic",
                    text = "Büyü gücün nerede yatıyor?",
                    options = listOf(
                        DecisionOption(
                            id = "wizard",
                            label = "Kitaplardan çalışarak öğrenilen büyü",
                            result = DndClass.WIZARD,
                        ),
                        DecisionOption(
                            id = "sorcerer",
                            label = "Kanından gelen doğuştan güç",
                            result = DndClass.SORCERER,
                        ),
                        DecisionOption(
                            id = "warlock",
                            label = "Yüce bir varlıkla yapılan anlaşma",
                            result = DndClass.WARLOCK,
                        ),
                        DecisionOption(
                            id = "bard",
                            label = "Müzik ve sanatın büyüsü",
                            result = DndClass.BARD,
                        ),
                    ),
                ),
            ),
        ),
    )
}
