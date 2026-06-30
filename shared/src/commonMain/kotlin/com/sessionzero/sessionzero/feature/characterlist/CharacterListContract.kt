package com.sessionzero.sessionzero.feature.characterlist

import com.sessionzero.sessionzero.data.dnd5e.ClassCategory

object CharacterListContract {

    data class State(
        val characters: List<CharacterListItem> = emptyList(),
        val isLoading: Boolean = true,
    )

    data class CharacterListItem(
        val id: Long,
        val name: String,
        val rpgSystem: String,
        val classCategory: ClassCategory?,
    )

    sealed interface Intent {
        data class CharacterClicked(val item: CharacterListItem) : Intent
        data object CreateCharacterClicked : Intent
    }

    sealed interface Effect {
        data class ShowClickedMessage(val characterName: String) : Effect
        data object NavigateToCreateCharacter : Effect
    }
}
