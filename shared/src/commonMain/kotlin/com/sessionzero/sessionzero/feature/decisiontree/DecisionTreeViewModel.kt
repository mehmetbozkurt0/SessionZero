package com.sessionzero.sessionzero.feature.decisiontree

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sessionzero.sessionzero.data.dnd5e.DecisionTreeData
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class DecisionTreeViewModel : ViewModel() {

    private val _state = MutableStateFlow(
        DecisionTreeContract.State(question = DecisionTreeData.rootQuestion)
    )
    val state: StateFlow<DecisionTreeContract.State> = _state.asStateFlow()

    private val _effect = Channel<DecisionTreeContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: DecisionTreeContract.Intent) {
        when (intent) {
            is DecisionTreeContract.Intent.OptionSelected -> {
                val option = intent.option
                when {
                    option.result != null -> viewModelScope.launch {
                        _effect.send(DecisionTreeContract.Effect.NavigateToCharacterSheet(option.result))
                    }
                    option.nextQuestion != null -> _state.value = _state.value.copy(
                        question = option.nextQuestion,
                        stepIndex = _state.value.stepIndex + 1,
                    )
                }
            }
            DecisionTreeContract.Intent.BackPressed -> {
                if (_state.value.stepIndex == 0) {
                    viewModelScope.launch {
                        _effect.send(DecisionTreeContract.Effect.NavigateBack)
                    }
                } else {
                    _state.value = _state.value.copy(
                        question = DecisionTreeData.rootQuestion,
                        stepIndex = 0,
                    )
                }
            }
        }
    }
}
