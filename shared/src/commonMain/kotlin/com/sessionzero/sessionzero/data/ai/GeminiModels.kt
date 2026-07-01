package com.sessionzero.sessionzero.data.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// — Gemini HTTP request models —

@Serializable
data class GeminiRequest(
    @SerialName("system_instruction")
    val systemInstruction: GeminiContent,
    val contents: List<GeminiUserContent>,
    val generationConfig: GeminiGenerationConfig = GeminiGenerationConfig(),
)

@Serializable
data class GeminiContent(
    val parts: List<GeminiPart>,
)

@Serializable
data class GeminiUserContent(
    val role: String = "user",
    val parts: List<GeminiPart>,
)

@Serializable
data class GeminiPart(
    val text: String,
)

@Serializable
data class GeminiGenerationConfig(
    val temperature: Float = 0.3f,
    val responseMimeType: String = "application/json",
)

// — Gemini HTTP response models —

@Serializable
data class GeminiResponse(
    val candidates: List<GeminiCandidate>,
)

@Serializable
data class GeminiCandidate(
    val content: GeminiContent,
)

// — Internal character JSON model (produced by Gemini) —

@Serializable
data class GeminiCharacterResult(
    val race: String,
    val className: String,
    val subclassSuggestion: String,
    val background: String,
    val stats: CharacterStatBlock,
    val derived: DerivedStatBlock,
    val storySummary: String,
)

@Serializable
data class CharacterStatBlock(
    val str: Int,
    val dex: Int,
    val con: Int,
    @SerialName("int") val intelligence: Int,
    val wis: Int,
    val cha: Int,
)

@Serializable
data class DerivedStatBlock(
    val hp: Int,
    val ac: Int,
    val initiative: Int,
)
