package com.sessionzero.sessionzero.feature.creationmode

object CreationModeContract {

    object State

    sealed interface Intent {
        data object AiModeSelected : Intent
        data object ManualModeSelected : Intent
        data object BackPressed : Intent
    }

    sealed interface Effect {
        data object NavigateToStoryAi : Effect
        data object NavigateToDecisionTree : Effect
        data object NavigateBack : Effect
    }
}
