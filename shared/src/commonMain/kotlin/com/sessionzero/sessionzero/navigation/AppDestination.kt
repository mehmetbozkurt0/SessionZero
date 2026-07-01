package com.sessionzero.sessionzero.navigation

object AppDestination {
    const val DASHBOARD = "dashboard"
    const val CHARACTER_LIST = "character_list"
    private const val SYSTEM_SELECTION_BASE = "system_selection"
    const val ARG_CREATION_METHOD = "creationMethod"
    const val SYSTEM_SELECTION = "$SYSTEM_SELECTION_BASE/{$ARG_CREATION_METHOD}"
    fun systemSelectionRoute(method: CreationMethod) = "$SYSTEM_SELECTION_BASE/${method.name}"
    const val STORY_AI = "story_ai"
    const val DECISION_TREE = "decision_tree"
    private const val CHARACTER_SHEET_BASE = "character_sheet"
    const val ARG_CLASS_ID = "classId"
    const val ARG_IS_BLANK = "isBlank"
    const val CHARACTER_SHEET = "$CHARACTER_SHEET_BASE/{$ARG_CLASS_ID}/{$ARG_IS_BLANK}"
    fun characterSheetRoute(classId: String, isBlank: Boolean = false) = "$CHARACTER_SHEET_BASE/$classId/$isBlank"

    private const val CHARACTER_DETAIL_BASE = "character_detail"
    const val ARG_CHARACTER_ID = "characterId"
    const val CHARACTER_DETAIL = "$CHARACTER_DETAIL_BASE/{$ARG_CHARACTER_ID}"
    fun characterDetailRoute(characterId: Long) = "$CHARACTER_DETAIL_BASE/$characterId"
}
