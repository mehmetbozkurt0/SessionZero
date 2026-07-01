package com.sessionzero.sessionzero.feature.creationmode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CreationModeViewModel : ViewModel() {

    private val _effect = Channel<CreationModeContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: CreationModeContract.Intent) {
        viewModelScope.launch {
            when (intent) {
                CreationModeContract.Intent.AiModeSelected ->
                    _effect.send(CreationModeContract.Effect.NavigateToStoryAi)
                CreationModeContract.Intent.ManualModeSelected ->
                    _effect.send(CreationModeContract.Effect.NavigateToDecisionTree)
                CreationModeContract.Intent.BackPressed ->
                    _effect.send(CreationModeContract.Effect.NavigateBack)
            }
        }
    }
}
