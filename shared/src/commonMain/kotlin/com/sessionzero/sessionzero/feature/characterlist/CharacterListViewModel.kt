package com.sessionzero.sessionzero.feature.characterlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sessionzero.sessionzero.data.character.CharacterRepository
import com.sessionzero.sessionzero.data.character.Dnd5eSystemData
import com.sessionzero.sessionzero.data.dnd5e.DndClass
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class CharacterListViewModel(
    private val repository: CharacterRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CharacterListContract.State())
    val state: StateFlow<CharacterListContract.State> = _state.asStateFlow()

    private val _effect = Channel<CharacterListContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private val json = Json { ignoreUnknownKeys = true }

    init {
        viewModelScope.launch {
            repository.getAllCharactersFlow().collect { records ->
                val items = records.map { record ->
                    val category = if (record.rpgSystem == "DND5E") {
                        runCatching {
                            val data = json.decodeFromString<Dnd5eSystemData>(record.systemData)
                            DndClass.fromId(data.className)?.category
                        }.getOrNull()
                    } else null

                    CharacterListContract.CharacterListItem(
                        id = record.id,
                        name = record.name,
                        rpgSystem = record.rpgSystem,
                        classCategory = category,
                    )
                }
                _state.update { it.copy(characters = items, isLoading = false) }
            }
        }
    }

    fun onIntent(intent: CharacterListContract.Intent) {
        when (intent) {
            is CharacterListContract.Intent.CharacterClicked -> {
                viewModelScope.launch {
                    _effect.send(CharacterListContract.Effect.NavigateToCharacterDetail(intent.item.id))
                }
            }
            CharacterListContract.Intent.CreateCharacterClicked -> {
                viewModelScope.launch {
                    _effect.send(CharacterListContract.Effect.NavigateToCreateCharacter)
                }
            }
        }
    }
}
