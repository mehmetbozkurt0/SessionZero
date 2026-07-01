package com.sessionzero.sessionzero.feature.storyai

object StoryAiContract {

    data class State(
        val story: String = "",
        val isAnalyzing: Boolean = false,
        val errorMessage: String? = null,
    )

    sealed interface Intent {
        data class StoryChanged(val text: String) : Intent
        data object AnalyzeClicked : Intent
        data object BackPressed : Intent
        data object ErrorDismissed : Intent
    }

    sealed interface Effect {
        data object NavigateBack : Effect
        data class NavigateToCharacterDetail(val characterId: Long) : Effect
    }
}
