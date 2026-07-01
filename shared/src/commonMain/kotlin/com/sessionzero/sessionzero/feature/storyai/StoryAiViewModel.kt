package com.sessionzero.sessionzero.feature.storyai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sessionzero.sessionzero.data.ai.AiRepository
import com.sessionzero.sessionzero.data.character.CharacterRepository
import com.sessionzero.sessionzero.data.character.Dnd5eSystemData
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class StoryAiViewModel(
    private val aiRepository: AiRepository,
    private val characterRepository: CharacterRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(StoryAiContract.State())
    val state: StateFlow<StoryAiContract.State> = _state.asStateFlow()

    private val _effect = Channel<StoryAiContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: StoryAiContract.Intent) {
        when (intent) {
            is StoryAiContract.Intent.StoryChanged ->
                _state.update { it.copy(story = intent.text, errorMessage = null) }

            StoryAiContract.Intent.AnalyzeClicked -> analyzeStory()

            StoryAiContract.Intent.BackPressed -> viewModelScope.launch {
                _effect.send(StoryAiContract.Effect.NavigateBack)
            }

            StoryAiContract.Intent.ErrorDismissed ->
                _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun analyzeStory() {
        val story = _state.value.story.trim()
        if (story.isBlank()) {
            _state.update { it.copy(errorMessage = "Please enter your character's story.") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isAnalyzing = true, errorMessage = null) }
            runCatching {
                val systemData: Dnd5eSystemData = aiRepository.generateCharacter(story)
                val json = Json.encodeToString(systemData)
                val characterId = characterRepository.saveCharacter(
                    name = systemData.displayName,
                    rpgSystem = "DND5E",
                    systemData = json,
                )
                characterId
            }
                .onSuccess { characterId ->
                    _effect.send(StoryAiContract.Effect.NavigateToCharacterDetail(characterId))
                }
                .onFailure { e ->
                    val message = when {
                        e.message?.contains("timeout", ignoreCase = true) == true ||
                        e.message?.contains("timed out", ignoreCase = true) == true ->
                            "The server didn't respond, please try again."
                        else -> e.message ?: "An error occurred."
                    }
                    _state.update { it.copy(errorMessage = message) }
                }
            _state.update { it.copy(isAnalyzing = false) }
        }
    }
}
