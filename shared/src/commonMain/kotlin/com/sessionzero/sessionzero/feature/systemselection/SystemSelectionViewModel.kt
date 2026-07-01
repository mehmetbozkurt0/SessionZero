package com.sessionzero.sessionzero.feature.systemselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sessionzero.sessionzero.data.dnd5e.DndClass
import com.sessionzero.sessionzero.navigation.CreationMethod
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SystemSelectionViewModel(
    creationMethod: CreationMethod,
) : ViewModel() {

    private val _state = MutableStateFlow(SystemSelectionContract.State(creationMethod = creationMethod))
    val state: StateFlow<SystemSelectionContract.State> = _state.asStateFlow()

    private val _effect = Channel<SystemSelectionContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: SystemSelectionContract.Intent) {
        when (intent) {
            is SystemSelectionContract.Intent.SystemSelected -> {
                if (intent.system == RpgSystem.DND5E) {
                    viewModelScope.launch {
                        val effect = when (_state.value.creationMethod) {
                            CreationMethod.STORY_AI -> SystemSelectionContract.Effect.NavigateToStoryAi
                            CreationMethod.GUIDED -> SystemSelectionContract.Effect.NavigateToDecisionTree
                            CreationMethod.BLANK_SHEET ->
                                SystemSelectionContract.Effect.NavigateToBlankSheet(DndClass.FIGHTER)
                        }
                        _effect.send(effect)
                    }
                }
            }
        }
    }
}
