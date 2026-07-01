package com.sessionzero.sessionzero.data.ai

import com.sessionzero.sessionzero.data.character.Dnd5eSystemData

interface AiRepository {
    suspend fun generateCharacter(story: String): Dnd5eSystemData
}
