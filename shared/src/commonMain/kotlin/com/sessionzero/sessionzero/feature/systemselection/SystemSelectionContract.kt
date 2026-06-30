package com.sessionzero.sessionzero.feature.systemselection

object SystemSelectionContract {

    data class State(
        val systems: List<RpgSystem> = RpgSystem.entries.toList(),
    )

    sealed interface Intent {
        data class SystemSelected(val system: RpgSystem) : Intent
    }

    sealed interface Effect {
        data object NavigateToDecisionTree : Effect
    }
}

enum class RpgSystem(val displayName: String, val isAvailable: Boolean) {
    DND5E("D&D 5E", true),
    FATE("Fate", false),
    COC("Call of Cthulhu", false),
}
