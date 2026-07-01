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
            ?: error("Gemini returned an empty response.")

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
You are an expert D&D 5E Dungeon Master. Analyze the story the user provides.

Rules:
* Distribute the 6 ability scores (STR, DEX, CON, INT, WIS, CHA) using the standard 27-point Point Buy system.
* Choose the Race that best fits the story and add the race's stat bonuses to the ability scores.
* Determine the Class, a Subclass Suggestion, and a Background.
* Following leveling rules, calculate Max HP (class's max hit die value + CON modifier), Initiative (DEX modifier), and AC (standard armor + DEX considerations).

IMPORTANT: The JSON below is only meant to show the data structure (schema). NEVER use its values verbatim. You must determine the Race, Class, Subclass, Stats, and every other value YOURSELF through a logical analysis based ENTIRELY on the story the user provided.

Respond with only JSON in the following format, no markdown or extra text:
{"race":"...","className":"...","subclassSuggestion":"...","background":"...","stats":{"str":0,"dex":0,"con":0,"int":0,"wis":0,"cha":0},"derived":{"hp":0,"ac":0,"initiative":0},"storySummary":"..."}
"""
