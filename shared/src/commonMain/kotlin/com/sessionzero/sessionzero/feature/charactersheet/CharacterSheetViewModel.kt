package com.sessionzero.sessionzero.feature.charactersheet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sessionzero.sessionzero.data.dnd5e.DndClass
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CharacterSheetViewModel(dndClass: DndClass) : ViewModel() {

    private val _state = MutableStateFlow(CharacterSheetContract.State(dndClass = dndClass))
    val state: StateFlow<CharacterSheetContract.State> = _state.asStateFlow()

    private val _effect = Channel<CharacterSheetContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: CharacterSheetContract.Intent) {
        when (intent) {
            CharacterSheetContract.Intent.StartOver -> viewModelScope.launch {
                _effect.send(CharacterSheetContract.Effect.NavigateToSystemSelection)
            }
        }
    }
}
