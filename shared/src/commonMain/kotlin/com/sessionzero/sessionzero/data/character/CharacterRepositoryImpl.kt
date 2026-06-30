package com.sessionzero.sessionzero.data.character

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.sessionzero.sessionzero.db.SessionZeroDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class CharacterRepositoryImpl(private val db: SessionZeroDb) : CharacterRepository {

    override suspend fun saveCharacter(name: String, rpgSystem: String, systemData: String) {
        withContext(Dispatchers.IO) {
            db.sessionZeroDbQueries.insertCharacter(name, rpgSystem, systemData)
        }
    }

    override fun getAllCharactersFlow(): Flow<List<CharacterRecord>> =
        db.sessionZeroDbQueries.selectAllCharacters()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { entity ->
                    CharacterRecord(
                        id = entity.id,
                        name = entity.name,
                        rpgSystem = entity.rpg_system,
                        systemData = entity.system_data,
                    )
                }
            }

    override suspend fun getAllCharacters(): List<CharacterRecord> =
        withContext(Dispatchers.IO) {
            db.sessionZeroDbQueries.selectAllCharacters().executeAsList().map { entity ->
                CharacterRecord(
                    id = entity.id,
                    name = entity.name,
                    rpgSystem = entity.rpg_system,
                    systemData = entity.system_data,
                )
            }
        }

    override suspend fun deleteCharacter(id: Long) {
        withContext(Dispatchers.IO) {
            db.sessionZeroDbQueries.deleteCharacter(id)
        }
    }
}
