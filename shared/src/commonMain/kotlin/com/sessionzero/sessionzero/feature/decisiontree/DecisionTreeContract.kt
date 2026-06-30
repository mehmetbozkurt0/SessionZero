package com.sessionzero.sessionzero.feature.decisiontree

import com.sessionzero.sessionzero.data.dnd5e.DecisionOption
import com.sessionzero.sessionzero.data.dnd5e.DecisionQuestion
import com.sessionzero.sessionzero.data.dnd5e.DndClass

object DecisionTreeContract {

    data class State(
        val question: DecisionQuestion,
        val stepIndex: Int = 0,
        val totalSteps: Int = 2,
    )

    sealed interface Intent {
        data class OptionSelected(val option: DecisionOption) : Intent
        data object BackPressed : Intent
    }

    sealed interface Effect {
        data class NavigateToCharacterSheet(val dndClass: DndClass) : Effect
        data object NavigateBack : Effect
    }
}
