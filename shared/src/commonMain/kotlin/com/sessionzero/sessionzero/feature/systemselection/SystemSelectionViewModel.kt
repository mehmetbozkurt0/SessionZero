package com.sessionzero.sessionzero.feature.systemselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SystemSelectionViewModel : ViewModel() {

    private val _state = MutableStateFlow(SystemSelectionContract.State())
    val state: StateFlow<SystemSelectionContract.State> = _state.asStateFlow()

    private val _effect = Channel<SystemSelectionContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: SystemSelectionContract.Intent) {
        when (intent) {
            is SystemSelectionContract.Intent.SystemSelected -> {
                if (intent.system == RpgSystem.DND5E) {
                    viewModelScope.launch {
                        _effect.send(SystemSelectionContract.Effect.NavigateToDecisionTree)
                    }
                }
            }
        }
    }
}
