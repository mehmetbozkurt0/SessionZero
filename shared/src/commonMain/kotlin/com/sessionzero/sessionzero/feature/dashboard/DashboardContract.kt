package com.sessionzero.sessionzero.feature.dashboard

import com.sessionzero.sessionzero.data.dnd5e.ClassCategory
import com.sessionzero.sessionzero.navigation.CreationMethod

object DashboardContract {

    data class State(
        val recentCharacters: List<RecentCharacter> = emptyList(),
        val isLoading: Boolean = true,
    )

    data class RecentCharacter(
        val id: Long,
        val name: String,
        val rpgSystem: String,
        val classCategory: ClassCategory?,
    )

    sealed interface Intent {
        data object StoryAiClicked : Intent
        data object GuidedCreationClicked : Intent
        data object BlankSheetClicked : Intent
        data class CharacterClicked(val id: Long) : Intent
        data object ViewAllClicked : Intent
    }

    sealed interface Effect {
        data class NavigateToSystemSelection(val method: CreationMethod) : Effect
        data class NavigateToCharacterDetail(val characterId: Long) : Effect
        data object NavigateToCharacterList : Effect
    }
}
