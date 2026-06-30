package com.sessionzero.sessionzero.data.dnd5e

enum class DndClass(
    val id: String,
    val displayName: String,
    val primaryStat: String,
    val hitDie: Int,
    val flavor: String,
) {
    BARBARIAN(
        id = "barbarian",
        displayName = "Barbar",
        primaryStat = "Güç (Strength)",
        hitDie = 12,
        flavor = "Öfkesiyle savaşır — zırhsız, kesintisiz, yıkıcı.",
    ),
    FIGHTER(
        id = "fighter",
        displayName = "Savaşçı",
        primaryStat = "Güç veya Çeviklik",
        hitDie = 10,
        flavor = "Silah ve taktik ustalığıyla her türlü dövüşe adapte olur.",
    ),
    MONK(
        id = "monk",
        displayName = "Keşiş",
        primaryStat = "Çeviklik ve Bilgelik",
        hitDie = 8,
        flavor = "Çıplak el ve mistik disiplinle beden ile zihni birleştirir.",
    ),
    ROGUE(
        id = "rogue",
        displayName = "Haydut",
        primaryStat = "Çeviklik (Dexterity)",
        hitDie = 8,
        flavor = "Gölgelerde hareket eder, tek vuruşta kritik hasar verir.",
    ),
    RANGER(
        id = "ranger",
        displayName = "Avcı",
        primaryStat = "Çeviklik ve Bilgelik",
        hitDie = 10,
        flavor = "Vahşi doğanın ustası; oka, tuzağa ve hayvan dostuna güvenir.",
    ),
    ARTIFICER(
        id = "artificer",
        displayName = "İcatçı",
        primaryStat = "Zeka (Intelligence)",
        hitDie = 8,
        flavor = "Mekanik icatlar ve büyülü aletlerle grubu güçlendirir.",
    ),
    CLERIC(
        id = "cleric",
        displayName = "Rahip",
        primaryStat = "Bilgelik (Wisdom)",
        hitDie = 8,
        flavor = "Tanrısının gücüyle iyileştirir ve kutsalın ışığıyla savaşır.",
    ),
    PALADIN(
        id = "paladin",
        displayName = "Paladin",
        primaryStat = "Güç ve Karizma",
        hitDie = 10,
        flavor = "Kutsal yeminle bağlı, ağır zırhlı ilahi şövalye.",
    ),
    DRUID(
        id = "druid",
        displayName = "Druid",
        primaryStat = "Bilgelik (Wisdom)",
        hitDie = 8,
        flavor = "Doğanın dengesini korur, hayvanlara ve elementlere dönüşür.",
    ),
    WIZARD(
        id = "wizard",
        displayName = "Büyücü",
        primaryStat = "Zeka (Intelligence)",
        hitDie = 6,
        flavor = "Yıllar süren çalışmayla öğrendiği büyülerle her şeyi değiştirir.",
    ),
    SORCERER(
        id = "sorcerer",
        displayName = "Sihirbaz",
        primaryStat = "Karizma (Charisma)",
        hitDie = 6,
        flavor = "Kanından gelen doğuştan güçle büyüyü içgüdüsel kullanır.",
    ),
    WARLOCK(
        id = "warlock",
        displayName = "Büyü Dini",
        primaryStat = "Karizma (Charisma)",
        hitDie = 8,
        flavor = "Yüce bir varlıkla yaptığı anlaşmanın karşılığında güç kazanır.",
    ),
    BARD(
        id = "bard",
        displayName = "Ozan",
        primaryStat = "Karizma (Charisma)",
        hitDie = 8,
        flavor = "Müzik ve sanatın büyüsüyle hem savaşır hem grubu büyüler.",
    );

    companion object {
        fun fromId(id: String): DndClass? = entries.find { it.id == id }
    }
}
