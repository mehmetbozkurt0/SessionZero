package com.sessionzero.sessionzero.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sessionzero.sessionzero.data.character.CharacterRepository
import com.sessionzero.sessionzero.data.character.Dnd5eSystemData
import com.sessionzero.sessionzero.data.dnd5e.DndClass
import com.sessionzero.sessionzero.navigation.CreationMethod
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class DashboardViewModel(
    private val repository: CharacterRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardContract.State())
    val state: StateFlow<DashboardContract.State> = _state.asStateFlow()

    private val _effect = Channel<DashboardContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private val json = Json { ignoreUnknownKeys = true }

    init {
        viewModelScope.launch {
            repository.getAllCharactersFlow().collect { records ->
                val recent = records
                    .sortedByDescending { it.id }
                    .take(3)
                    .map { record ->
                        val category = if (record.rpgSystem == "DND5E") {
                            runCatching {
                                val data = json.decodeFromString<Dnd5eSystemData>(record.systemData)
                                DndClass.fromId(data.className)?.category
                            }.getOrNull()
                        } else null

                        DashboardContract.RecentCharacter(
                            id = record.id,
                            name = record.name,
                            rpgSystem = record.rpgSystem,
                            classCategory = category,
                        )
                    }
                _state.update { it.copy(recentCharacters = recent, isLoading = false) }
            }
        }
    }

    fun onIntent(intent: DashboardContract.Intent) {
        viewModelScope.launch {
            when (intent) {
                DashboardContract.Intent.StoryAiClicked ->
                    _effect.send(DashboardContract.Effect.NavigateToSystemSelection(CreationMethod.STORY_AI))

                DashboardContract.Intent.GuidedCreationClicked ->
                    _effect.send(DashboardContract.Effect.NavigateToSystemSelection(CreationMethod.GUIDED))

                DashboardContract.Intent.BlankSheetClicked ->
                    _effect.send(DashboardContract.Effect.NavigateToSystemSelection(CreationMethod.BLANK_SHEET))

                is DashboardContract.Intent.CharacterClicked ->
                    _effect.send(DashboardContract.Effect.NavigateToCharacterDetail(intent.id))

                DashboardContract.Intent.ViewAllClicked ->
                    _effect.send(DashboardContract.Effect.NavigateToCharacterList)
            }
        }
    }
}
