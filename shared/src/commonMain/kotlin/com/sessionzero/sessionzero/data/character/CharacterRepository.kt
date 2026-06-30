package com.sessionzero.sessionzero.data.character

import kotlinx.coroutines.flow.Flow

interface CharacterRepository {
    suspend fun saveCharacter(name: String, rpgSystem: String, systemData: String)
    fun getAllCharactersFlow(): Flow<List<CharacterRecord>>
    suspend fun getAllCharacters(): List<CharacterRecord>
    suspend fun deleteCharacter(id: Long)
}

data class CharacterRecord(
    val id: Long,
    val name: String,
    val rpgSystem: String,
    val systemData: String,
)
