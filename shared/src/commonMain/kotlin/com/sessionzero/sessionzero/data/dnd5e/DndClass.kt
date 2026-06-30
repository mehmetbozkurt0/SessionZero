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
        displayName = "Barbar",
        primaryStat = "Güç (Strength)",
        hitDie = 12,
        flavor = "Öfkesiyle savaşır — zırhsız, kesintisiz, yıkıcı.",
        category = ClassCategory.PHYSICAL,
        startingEquipment = listOf(
            "Büyük balta (greataxe)",
            "İki el çekici",
            "Dört el bombası",
            "Gezgin kıyafetleri",
        ),
    ),
    FIGHTER(
        id = "fighter",
        displayName = "Savaşçı",
        primaryStat = "Güç veya Çeviklik",
        hitDie = 10,
        flavor = "Silah ve taktik ustalığıyla her türlü dövüşe adapte olur.",
        category = ClassCategory.PHYSICAL,
        startingEquipment = listOf(
            "Zincir zırh",
            "Uzun kılıç ve çelik kalkan",
            "Beş dart",
        ),
    ),
    MONK(
        id = "monk",
        displayName = "Keşiş",
        primaryStat = "Çeviklik ve Bilgelik",
        hitDie = 8,
        flavor = "Çıplak el ve mistik disiplinle beden ile zihni birleştirir.",
        category = ClassCategory.PHYSICAL,
        startingEquipment = listOf(
            "Kısa kılıç",
            "On dart",
            "Macera kıyafetleri",
        ),
    ),
    ROGUE(
        id = "rogue",
        displayName = "Haydut",
        primaryStat = "Çeviklik (Dexterity)",
        hitDie = 8,
        flavor = "Gölgelerde hareket eder, tek vuruşta kritik hasar verir.",
        category = ClassCategory.STEALTH,
        startingEquipment = listOf(
            "Deri zırh",
            "İki kısa kılıç",
            "Hırsız aletleri (thieves' tools)",
        ),
    ),
    RANGER(
        id = "ranger",
        displayName = "Avcı",
        primaryStat = "Çeviklik ve Bilgelik",
        hitDie = 10,
        flavor = "Vahşi doğanın ustası; oka, tuzağa ve hayvan dostuna güvenir.",
        category = ClassCategory.STEALTH,
        startingEquipment = listOf(
            "Deri zırh",
            "Uzun yay ve yirmi ok",
            "İki kısa kılıç",
        ),
    ),
    ARTIFICER(
        id = "artificer",
        displayName = "İcatçı",
        primaryStat = "Zeka (Intelligence)",
        hitDie = 8,
        flavor = "Mekanik icatlar ve büyülü aletlerle grubu güçlendirir.",
        category = ClassCategory.STEALTH,
        startingEquipment = listOf(
            "Deri zırh",
            "İki el çekici",
            "Alet çantası (tinker's tools)",
        ),
    ),
    CLERIC(
        id = "cleric",
        displayName = "Rahip",
        primaryStat = "Bilgelik (Wisdom)",
        hitDie = 8,
        flavor = "Tanrısının gücüyle iyileştirir ve kutsalın ışığıyla savaşır.",
        category = ClassCategory.DIVINE,
        startingEquipment = listOf(
            "Zincir zırh",
            "Çelik kalkan",
            "Savaş çekici",
            "Kutsal sembol",
        ),
    ),
    PALADIN(
        id = "paladin",
        displayName = "Paladin",
        primaryStat = "Güç ve Karizma",
        hitDie = 10,
        flavor = "Kutsal yeminle bağlı, ağır zırhlı ilahi şövalye.",
        category = ClassCategory.DIVINE,
        startingEquipment = listOf(
            "Plaka zırh",
            "Uzun kılıç ve kutsal kalkan",
            "Kutsal sembol",
        ),
    ),
    DRUID(
        id = "druid",
        displayName = "Druid",
        primaryStat = "Bilgelik (Wisdom)",
        hitDie = 8,
        flavor = "Doğanın dengesini korur, hayvanlara ve elementlere dönüşür.",
        category = ClassCategory.DIVINE,
        startingEquipment = listOf(
            "Ahşap kalkan",
            "Deri zırh",
            "Mistik odak (druidic focus)",
        ),
    ),
    WIZARD(
        id = "wizard",
        displayName = "Büyücü",
        primaryStat = "Zeka (Intelligence)",
        hitDie = 6,
        flavor = "Yıllar süren çalışmayla öğrendiği büyülerle her şeyi değiştirir.",
        category = ClassCategory.MAGIC,
        startingEquipment = listOf(
            "Büyü kitabı (spellbook)",
            "Arkanik odak (arcane focus)",
            "Macera kıyafetleri",
        ),
    ),
    SORCERER(
        id = "sorcerer",
        displayName = "Sihirbaz",
        primaryStat = "Karizma (Charisma)",
        hitDie = 6,
        flavor = "Kanından gelen doğuştan güçle büyüyü içgüdüsel kullanır.",
        category = ClassCategory.MAGIC,
        startingEquipment = listOf(
            "Hafif arbalest ve yirmi bolt",
            "Arkanik odak",
            "Macera kıyafetleri",
        ),
    ),
    WARLOCK(
        id = "warlock",
        displayName = "Büyü Dini",
        primaryStat = "Karizma (Charisma)",
        hitDie = 8,
        flavor = "Yüce bir varlıkla yaptığı anlaşmanın karşılığında güç kazanır.",
        category = ClassCategory.MAGIC,
        startingEquipment = listOf(
            "Deri zırh",
            "Basit silah",
            "Arkanik odak",
        ),
    ),
    BARD(
        id = "bard",
        displayName = "Ozan",
        primaryStat = "Karizma (Charisma)",
        hitDie = 8,
        flavor = "Müzik ve sanatın büyüsüyle hem savaşır hem grubu büyüler.",
        category = ClassCategory.MAGIC,
        startingEquipment = listOf(
            "Deri zırh",
            "Rapiyer",
            "Müzik aleti (lüt)",
        ),
    );

    companion object {
        fun fromId(id: String): DndClass? = entries.find { it.id == id }
    }
}
