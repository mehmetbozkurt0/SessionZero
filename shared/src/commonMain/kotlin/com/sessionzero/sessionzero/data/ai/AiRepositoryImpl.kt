package com.sessionzero.sessionzero.data.ai

import com.sessionzero.sessionzero.BuildKonfig
import com.sessionzero.sessionzero.data.character.ActionData
import com.sessionzero.sessionzero.data.character.Dnd5eSystemData
import com.sessionzero.sessionzero.data.dnd5e.DndClass
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class AiRepositoryImpl : AiRepository {

    private val json = Json { ignoreUnknownKeys = true }

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(json)
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 60_000
            connectTimeoutMillis = 60_000
            socketTimeoutMillis  = 60_000
        }
    }

    override suspend fun generateCharacter(story: String): Dnd5eSystemData {
        val response: GeminiResponse = client.post(
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=${BuildKonfig.GEMINI_API_KEY}"
        ) {
            contentType(ContentType.Application.Json)
            setBody(
                GeminiRequest(
                    systemInstruction = GeminiContent(
                        parts = listOf(GeminiPart(SYSTEM_INSTRUCTION)),
                    ),
                    contents = listOf(
                        GeminiUserContent(
                            parts = listOf(GeminiPart(story)),
                        ),
                    ),
                )
            )
        }.body()

        val rawText = response.candidates
            .firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: error("Gemini boş yanıt döndürdü.")

        val cleanedText = rawText.trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        val result = json.decodeFromString<GeminiCharacterResult>(cleanedText)
        return result.toDnd5eSystemData()
    }

    private fun GeminiCharacterResult.toDnd5eSystemData(): Dnd5eSystemData {
        val dndClass = DndClass.fromId(className.lowercase()) ?: DndClass.FIGHTER
        return Dnd5eSystemData(
            className = dndClass.id,
            displayName = dndClass.displayName,
            primaryStat = dndClass.primaryStat,
            hp = derived.hp,
            armorClass = derived.ac,
            initiative = derived.initiative,
            speedFt = 30,
            abilityScores = mapOf(
                "STR" to stats.str,
                "DEX" to stats.dex,
                "CON" to stats.con,
                "INT" to stats.intelligence,
                "WIS" to stats.wis,
                "CHA" to stats.cha,
            ),
            actions = emptyList(),
            startingEquipment = dndClass.startingEquipment,
            backstory = storySummary,
            race = race,
            subclassSuggestion = subclassSuggestion,
            background = background,
        )
    }
}

private const val SYSTEM_INSTRUCTION = """
Sen uzman bir D&D 5E Dungeon Master'sın. Kullanıcının verdiği hikayeyi analiz et.

Kurallar:
* Standart 27 puanlık Point Buy sistemini kullanarak 6 temel niteliği (STR, DEX, CON, INT, WIS, CHA) dağıt.
* Hikayeye en uygun Irkı (Race) seç ve ırkın stat bonuslarını temel niteliklere ekle.
* Sınıfı (Class), Alt Sınıf Önerisini (Subclass Suggestion) ve Geçmişi (Background) belirle.
* Seviye kurallarına göre Maksimum HP'yi (Sınıfın Max Zar Değeri + CON Değiştiricisi), İnisiyatifi (DEX Değiştiricisi) ve AC'yi (Standart zırh + DEX durumu) hesapla.

ÖNEMLİ: Aşağıdaki JSON sadece veri yapısını (schema) göstermek içindir. İçindeki değerleri ASLA birebir kullanma. Irk, Sınıf, Alt Sınıf, Statlar ve diğer tüm değerleri TAMAMEN kullanıcının verdiği hikayeye göre mantıksal bir analiz yaparak KENDİN belirlemelisin.

Sadece şu formattaki JSON çıktısını ver, markdown veya ekstra metin kullanma:
{"race":"...","className":"...","subclassSuggestion":"...","background":"...","stats":{"str":0,"dex":0,"con":0,"int":0,"wis":0,"cha":0},"derived":{"hp":0,"ac":0,"initiative":0},"storySummary":"..."}
"""
