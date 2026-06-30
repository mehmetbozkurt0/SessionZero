package com.sessionzero.sessionzero.feature.charactersheet

import com.sessionzero.sessionzero.data.dnd5e.DndClass

object CharacterSheetContract {

    data class State(
        val dndClass: DndClass,
        val level: Int = 1,
        val baseHp: Int = dndClass.hitDie,
    )

    sealed interface Intent {
        data object StartOver : Intent
    }

    sealed interface Effect {
        data object NavigateToSystemSelection : Effect
    }
}
